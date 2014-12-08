package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.SchedulerAgent;

public class ADOPTBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
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
