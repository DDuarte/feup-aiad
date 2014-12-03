package pt.up.fe.aiad.scheduler;


import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SchedulerAgent extends Agent {
    public enum Type {
        ABT
    }

    /**
     * List of all the events this agent is participating in
     */
    public ArrayList<ScheduleEvent> _events;
    /**
     * For each of the events this agent is participating in, lists other participants
     */
    public Map<String, ArrayList<AID>> _participants;

    public ObservableList<String> _allAgents;

    private Type _agentType;

    public static void main(String[] args) {
        if (args.length == 0) {
            String[] ar = {"-container"};
            jade.Boot.main(ar);
        }
        else
            jade.Boot.main(args);
    }

    public SchedulerAgent(Type agentType) {
        _events = new ArrayList<>();
        _participants = new HashMap<>();
        _allAgents = FXCollections.observableArrayList();
        _agentType = agentType;
    }

    @Override
    public void setup() {
        System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("scheduler");
        sd.setName("iScheduler");
        dfd.addServices(sd);
        try {
            DFAgentDescription template = new DFAgentDescription();

            addBehaviour(new SubscriptionInitiator(this, DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
                protected void handleInform(ACLMessage inform) {
                    try {
                        DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());

                        for (DFAgentDescription dfd1 : dfds) {
                            if (!dfd1.getName().toString().equals(getAID().toString())) {
                                Platform.runLater(() -> _allAgents.add(dfd1.getName().getName()));
                                System.out.println("I, agent " + getAID().getName() + ", have found agent " + dfd1.getName().getName() + ".");
                            }
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                }
            });

            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }


        /* This must be done only after SETUP phase ends
        switch (_agentType) {
            case ABT:
                addBehaviour(new ABTBehaviour());
                break;
            default:
                System.err.print("Invalid type selected");
                doDelete();
                break;
        }
        */
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        //TODO Close the GUI if necessary

        // Printout a dismissal message
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }
}
