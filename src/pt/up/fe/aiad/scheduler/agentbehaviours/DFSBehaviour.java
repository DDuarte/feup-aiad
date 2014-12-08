package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.beans.property.SimpleStringProperty;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;

import java.util.TreeSet;

public class DFSBehaviour extends SimpleBehaviour {
    private boolean allFinished = false;
    private SchedulerAgent _agent;
    private SchedulerAgent.Type _type;

    public DFSBehaviour(SchedulerAgent.Type type, SimpleStringProperty leader) {
        _type = type;
        _leaderProperty = leader;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
        _leader = _leaderProperty.getValue();

        for (ScheduleEvent event : _agent._events) {
            for (AID agent : event._participants) {
                if (!_agent.getAID().equals(agent)) {
                    _neighbours.add(agent.getName());
                }
            }
        }

        if (_neighbours.isEmpty()) {
            allFinished = true;
        } else {
            if (_agent.getName().equals(_leader)) {
                _openX.addAll(_neighbours);
                String y0 = _openX.first();

                _openX.remove(y0);
                _children.add(y0);

                sendChild(y0);
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
            String yi = msg.getSender().getName();
            if (_openX.isEmpty()) {
                _openX.addAll(_neighbours);
                _openX.remove(yi);
                _parentX = yi;
            } else {
                int separatorIndex = msg.getContent().indexOf('-');
                if (separatorIndex != -1) {
                    String str = msg.getContent();
                    String[] strs = str.split("-", 2);
                    switch (strs[0]) {
                        case "CHILD":
                            if (_openX.contains(yi)) {
                                _openX.remove(yi);
                                _pseudoChildren.add(yi);
                                sendPseudo(yi);
                                return;
                            }
                            break;
                        case "PSEUDO":
                            _children.remove(yi);
                            _pseudoParents.add(yi);
                            break;
                        default:
                            System.err.println("Received an invalid message type.");
                            break;
                    }
                }
                else {
                    System.err.println("Received an invalid message");
                }
            }

            if (!_openX.isEmpty()) {
                String yj = _openX.first();
                _openX.remove(yi);
                _children.add(yi);
                sendChild(yj);
            } else {
                if (!_agent.getName().equals(_leader)) {
                    sendChild(_parentX);
                }

                allFinished = true;
            }
        } else {
            block();
        }
    }

    private TreeSet<String> _neighbours = new TreeSet<>();
    private TreeSet<String> _openX = new TreeSet<>();
    private TreeSet<String> _children = new TreeSet<>();
    private TreeSet<String> _pseudoChildren = new TreeSet<>();
    private TreeSet<String> _pseudoParents = new TreeSet<>();
    private String _parentX;
    private String _leader;

    private SimpleStringProperty _leaderProperty;

    private void sendChild(String agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.addReceiver(new AID(agent, true));
        msg.setContent("CHILD-");
        msg.setConversationId("DFS");
        _agent.send(msg);
    }

    private void sendPseudo(String agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.addReceiver(new AID(agent, true));
        msg.setContent("PSEUDO-");
        msg.setConversationId("DFS");
        _agent.send(msg);
    }

    @Override
    public boolean done() {
        if (allFinished) {
            switch (_type) {
                case ADOPT:
                    _agent.addBehaviour(new ADOPTBehaviour(_leader, _parentX, _children, _pseudoChildren, _pseudoParents));
                    break;
                default:
                    System.err.println("DFSBehaviour: Unhandled type " + _type);
                    break;
            }
            return true;
        }
        return false;
    }
}
