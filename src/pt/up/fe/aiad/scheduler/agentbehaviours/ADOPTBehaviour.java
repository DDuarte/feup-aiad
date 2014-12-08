package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.SchedulerAgent;

import java.util.Arrays;
import java.util.TreeSet;

public class ADOPTBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;

    private TreeSet<String> _children = new TreeSet<>();
    private TreeSet<String> _pseudoChildren = new TreeSet<>();
    private TreeSet<String> _pseudoParents = new TreeSet<>();
    private String _parentX;
    private String _leader;

    public ADOPTBehaviour(String leader, String parent, TreeSet<String> children, TreeSet<String> pseudoChildren, TreeSet<String> pseudoParents) {
        _leader = leader;
        _parentX = parent;
        _children = children;
        _pseudoChildren = pseudoChildren;
        _pseudoParents = pseudoParents;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;

        System.err.println("ADOPT(" + _agent.getLocalName() + "): leader: " + _leader);
        System.err.println("ADOPT(" + _agent.getLocalName() + "): parent: " + _parentX);
        System.err.println("ADOPT(" + _agent.getLocalName() + "): children: " +  Arrays.toString(_children.toArray()));
        System.err.println("ADOPT(" + _agent.getLocalName() + "): pchildren: " +  Arrays.toString(_pseudoChildren.toArray()));
        System.err.println("ADOPT(" + _agent.getLocalName() + "): pparent: " + Arrays.toString(_pseudoParents.toArray()));

        if (_agent._events.isEmpty()) {
            allFinished = true;
            _agent.finishedAlgorithm();
            return;
        }

        // TODO
    }

    @Override
    public void action() {
        if (allFinished)
            return;

        MessageTemplate mt = MessageTemplate.MatchConversationId("ADOPT");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 3 /*?*/);
                switch (strs[0]) {
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

    @Override
    public boolean done() {
        return allFinished;
    }
}
