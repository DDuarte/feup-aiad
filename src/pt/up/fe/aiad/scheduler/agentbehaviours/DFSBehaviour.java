package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.beans.property.SimpleStringProperty;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class DFSBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;
    private Behaviour _nextBehaviour;

    public DFSBehaviour(Behaviour behaviour) {
        _nextBehaviour = behaviour;

        _leader.addListener((observable, oldValue, newValue) -> {
            System.err.println(_agent.getName() + " - the new leader is: " + newValue + " - (old: " + oldValue + ")");
        });
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;

        for (ScheduleEvent event : _agent._events) {
            for (AID agent : event._participants) {
                if (_agent.getAID().compareTo(agent) < 0) {
                    _lowerAgents.add(agent.getName());
                } else if (_agent.getAID().compareTo(agent) > 0) {
                    _upperAgents.add(agent.getName());
                }
            }
        }

        if (_upperAgents.isEmpty()) {
            if (!_lowerAgents.isEmpty()) {
                sendLeaderPropose(_agent.getName());
            } else {
                _leader.setValue(_agent.getName());
                allFinished = true;
            }
        }
    }

    @Override
    public void action() {
        if (allFinished)
            return;

        MessageTemplate mt = MessageTemplate.MatchConversationId("DFS");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 2);
                switch (strs[0]) {
                    case "LEADER_P":
                        receiveLeaderPropose(msg.getSender().getName(), strs[1]);
                        break;
                    case "LEADER_C":
                        receiveLeaderChoice(strs[1]);
                        break;
                    default:
                        System.err.println("Received an invalid message type.");
                        break;
                }
            }
            else {
                System.err.println("Received an invalid message");
            }
        } else {
            block();
        }
    }

    private Map<String, String> _assignments = new HashMap<>();
    private TreeSet<String> _lowerAgents = new TreeSet<>();
    private TreeSet<String> _upperAgents = new TreeSet<>();
    private SimpleStringProperty _leader = new SimpleStringProperty();

    private void receiveLeaderPropose(String sender, String agent) {
        _assignments.put(sender, agent);

        if (_assignments.keySet().containsAll(_upperAgents)) {
            TreeSet<String> orderedLeaders = new TreeSet<>(_assignments.values());
            String leader = orderedLeaders.first();

            if (!_lowerAgents.isEmpty()) {
                sendLeaderPropose(leader);
            } else if (!_upperAgents.isEmpty()) {
                _leader.setValue(leader);
                sendLeaderChoice(leader);
            }
        }
    }

    private void receiveLeaderChoice(String agent) {
        _leader.setValue(agent);

        if (!_upperAgents.isEmpty()) {
            sendLeaderChoice(agent);
        }
    }

    private void sendLeaderChoice(String leader) {
        // to upper

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        for (String agent : _upperAgents) {
            msg.addReceiver(new AID(agent, true));
        }
        msg.setContent("LEADER_C-" + leader);
        msg.setConversationId("DFS");
        _agent.send(msg);
    }

    private void sendLeaderPropose(String leader) {
        // to lower

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        for (String agent : _lowerAgents) {
            msg.addReceiver(new AID(agent, true));
        }
        msg.setContent("LEADER_P-" + leader);
        msg.setConversationId("DFS");
        _agent.send(msg);
    }

    @Override
    public boolean done() {
        if (allFinished) {
            _agent.addBehaviour(_nextBehaviour);
            return true;
        }
        return false;
    }
}
