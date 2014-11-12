package pt.up.fe.aiad.scheduler.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.Message;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class ABTAgent extends Agent {

    private final ArrayList<ScheduleEvent> _events;
    private final TreeSet<AID> _participants;
    private final TreeSet<AID> _links;

    public ABTAgent(ArrayList<ScheduleEvent> events, TreeSet<AID> participants) {
        _events = events;
        _participants = participants;
        _links = new TreeSet<AID>();
    }

    public class NoGoodBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(Message.ACL[Message.Type.NoGood.ordinal()]);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {

            } else {
                block();
            }
        }
    }

    public class IsOkayBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(Message.ACL[Message.Type.IsOkay.ordinal()]);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {

            } else {
                block();
            }
        }
    }

    public class AddLinkBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(Message.ACL[Message.Type.AddLink.ordinal()]);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                AID sender = msg.getSender();
                _links.add(sender);
                System.out.println("AddLinkBehaviour: " + myAgent.getAID() + " adds link to " + sender);
            } else {
                block();
            }
        }
    }

    public void SendIsOkayToNext(Map<String, TimeInterval> events) {
        AID next = _links.higher(getAID());
        if (next == null) {
            System.out.println("SendIsOkayToNext: " + getAID() + " does not have a next agent.");
            return;
        }

        SendIsOkay(next, events);
    }

    public void SendIsOkay(AID agent, Map<String, TimeInterval> events) {
        String json = Serializer.EventsToJSON(events);

        ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.IsOkay.ordinal()]);
        msg.addReceiver(agent);
        msg.setContent(json);
        msg.setConversationId("schedule-align");
        send(msg);

        System.out.println("SendIsOkay: " + getAID() + " sent " + json + " to " + agent);
    }

    public void SendAddLink(AID agent) {
        ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.AddLink.ordinal()]);
        msg.addReceiver(agent);
        msg.setConversationId("schedule-align");
        send(msg);

        System.out.println("SendAddLink: " + getAID() + " sent add link to " + agent);
    }

    public void SendNoGood(AID agent, Map<AID, Map<String, TimeInterval>> eventsAgentView) {
        String json = Serializer.EventsAgentViewToJSON(eventsAgentView);

        ACLMessage msg = new ACLMessage(Message.ACL[Message.Type.NoGood.ordinal()]);
        msg.addReceiver(agent);
        msg.setContent(json);
        msg.setConversationId("schedule-align");
        send(msg);

        System.out.println("SendNoGood: " + getAID() + " sent " + json + " to " + agent);
    }

    @Override
    public void setup() {
        //TODO Create and show the GUI

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("scheduler");
        sd.setName("iScheduler");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new NoGoodBehaviour());
        addBehaviour(new IsOkayBehaviour());
        addBehaviour(new AddLinkBehaviour());
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        //TODO Close the GUI if necessary

        // Printout a dismissal message
        System.out.println("Agent "+getAID().getName()+" terminating.");
    }
}
