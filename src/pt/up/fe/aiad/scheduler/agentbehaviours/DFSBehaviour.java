package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.beans.property.SimpleStringProperty;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;

import java.util.HashMap;
import java.util.TreeSet;

public class DFSBehaviour extends SimpleBehaviour {
    private SchedulerAgent _agent;
    private SchedulerAgent.Type _type;

    public DFSBehaviour(SchedulerAgent.Type type, SimpleStringProperty leader) {
        _type = type;
        _leaderProperty = leader;
    }

    public HashMap<String, VirtualAgent> _virtualAgents = new HashMap<>();

    public static class VirtualAgent {

        private boolean _finished;
        private final DFSBehaviour _masterInstance;
        public ScheduleEvent _event;
        private SchedulerAgent _masterAgent;

        private TreeSet<String> _neighbours = new TreeSet<>();
        private TreeSet<String> _openX = new TreeSet<>();
        public TreeSet<String> _children = new TreeSet<>();
        public TreeSet<String> _pseudoChildren = new TreeSet<>();
        public TreeSet<String> _pseudoParents = new TreeSet<>();
        public String _parentX;

        public VirtualAgent(ScheduleEvent scheduleEvent, SchedulerAgent schedulerAgent, DFSBehaviour masterInstance) {
            _masterInstance = masterInstance;
            _event = scheduleEvent;
            _masterAgent = schedulerAgent;

            for (ScheduleEvent event : _masterAgent._events) {
                for (AID agent : event._participants) {
                    if (!_masterAgent.getAID().equals(agent)) {
                        _neighbours.add(agent.getName() + "-" + event.getName());
                    }
                }
            }

            _openX.addAll(_neighbours);
            if ((_masterAgent.getName() + "-" + _event.getName()).equals(_masterInstance._leader)) {
                String n = _openX.first();
                _openX.remove(n);
                _children.add(n);
                sendChild(n);
            }
        }

        public String getName() {
            return _masterAgent.getName() + "-" + _event.getName();
        }

        private void sendChild(String agent /* A-1 */) {

            String[] name = agent.split("-", 2);

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(new AID(name[0], true));
            msg.setContent("CHILD-" + _event.getName() + "-" + agent);
            msg.setConversationId("DFS");
            _masterAgent.send(msg);
        }

        private void sendPseudo(String agent) {

            String[] name = agent.split("-", 2);

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(new AID(name[0], true));
            msg.setContent("PSEUDO-" + _event.getName() + "-" + agent);
            msg.setConversationId("DFS");
            _masterAgent.send(msg);
        }

    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
        _leader = _leaderProperty.getValue() + "-" + _agent._events.get(0).getName();

        for (ScheduleEvent ev : _agent._events) {
            _virtualAgents.put(_agent.getName() + "-" + ev.getName(), new VirtualAgent(ev, _agent, this));
        }
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId("DFS");
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String yi = msg.getSender().getName();
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 3);
                yi += "-" + strs[1];
                String name = strs[2];

                if (strs[0].equals("CHILD") && _virtualAgents.get(name)._parentX == null && !_virtualAgents.get(name).getName().equals(_leader)) {
                    _virtualAgents.get(name)._openX.remove(yi);
                    _virtualAgents.get(name)._parentX = yi;
                } else if (strs[0].equals("CHILD") && _virtualAgents.get(name)._openX.contains(yi)) {
                    _virtualAgents.get(name)._openX.remove(yi);
                    _virtualAgents.get(name)._pseudoChildren.add(yi);
                    _virtualAgents.get(name).sendPseudo(yi);
                }

                if (strs[0].equals("PSEUDO")) {
                    _virtualAgents.get(name)._children.remove(yi);
                    _virtualAgents.get(name). _pseudoParents.add(yi);
                }
                if (!_virtualAgents.get(name)._openX.isEmpty()) {
                    String n = _virtualAgents.get(name)._openX.first();
                    _virtualAgents.get(name)._openX.remove(n);
                    _virtualAgents.get(name). _children.add(n);
                    _virtualAgents.get(name).sendChild(n);
                } else {
                    if (!_virtualAgents.get(name).getName().equals(_leader))
                        _virtualAgents.get(name).sendChild(_virtualAgents.get(name)._parentX);
                    _virtualAgents.get(name)._finished = true;
                }
            }
            else {
                System.err.println("Received an invalid message");
            }
        } else {
            block();
        }

    }

    private SimpleStringProperty _leaderProperty;
    private String _leader;

    @Override
    public boolean done() {
        for (VirtualAgent va : _virtualAgents.values()) {
            if (!va._finished)
                return false;
        }

        switch (_type) {
            case ADOPT:
                _agent.addBehaviour(new ADOPTBehaviour(_leader, _virtualAgents));
                break;
            default:
                System.err.println("DFSBehaviour: Unhandled type " + _type);
                break;
        }
        return true;
    }
}
