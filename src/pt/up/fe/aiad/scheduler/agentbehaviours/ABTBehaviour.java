package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Map;

public class ABTBehaviour extends SimpleBehaviour {
    //TODO private TreeSet<AID> _links;
    //TODO: create agent view and link structure

    private boolean isFinished = false;
    private SchedulerAgent _agent;

    public ABTBehaviour (/*TreeSet<AID> initialLinks*/) {
        //_links = initialLinks;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
        if (_agent._events.isEmpty()) {
            isFinished = true;
            _agent.finishedAlgorithm();
            return;
        }
        //TODO initialize variables and send OKAYS
    }

    @Override
    public void action() {
        if (isFinished)
            return;
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String msgType = msg.getContent().substring(0, msg.getContent().indexOf('-'));
                if (msgType.equals("OK?")) {

                }
                else if (msgType.equals("NOGOOD")) {

                }
                else if (msgType.equals("LINK")) {

                }
                else {
                    System.err.println("Received an invalid message type.");
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
        return isFinished;
    }

    public void SendIsOkay(AID agent, Map<String, TimeInterval> events) {
        /*String json = Serializer.EventsToJSON(events);

        ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.IsOkay.ordinal()]);
        msg.addReceiver(agent);
        msg.setContent("OK?-" + json);
        msg.setConversationId("schedule-align");
        getAgent().send(msg);

        System.out.println("SendIsOkay: " + getAgent().getAID() + " sent " + json + " to " + agent);*/
    }

    public void SendAddLink(AID agent) {
        /*ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.AddLink.ordinal()]);
        msg.addReceiver(agent);
        msg.setContent("LINK-");
        msg.setConversationId("schedule-align");
        getAgent().send(msg);

        System.out.println("SendAddLink: " + getAgent().getAID() + " sent add link to " + agent);*/
    }

    public void SendNoGood(AID agent, Map<AID, Map<String, TimeInterval>> eventsAgentView) {
        /*String json = Serializer.EventsAgentViewToJSON(eventsAgentView);

        ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.NoGood.ordinal()]);
        msg.addReceiver(agent);
        msg.setContent("NOGOOD-" + json);
        msg.setConversationId("schedule-align");
        getAgent().send(msg);

        System.out.println("SendNoGood: " + getAgent().getAID() + " sent " + json + " to " + agent);*/
    }
}
