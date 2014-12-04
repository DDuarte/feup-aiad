package pt.up.fe.aiad.scheduler;


import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.Notifications;

import java.util.*;


public class SchedulerAgent extends Agent {
    public enum Type {
        ABT
    }

    /**
     * List of all the events this agent is participating in
     */
    public ArrayList<ScheduleEvent> _events = new ArrayList<>();
    /**
     * For each of the events this agent is participating in, lists other participants
     */
    public Map<String, ArrayList<AID>> _participants = new HashMap<>();

    /**
     * All agent names excluding self
     */
    public ObservableList<String> _otherAgents = FXCollections.observableArrayList();
    public Set<AID> _allAgents = new TreeSet<>();

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
                                if (dfd1.getAllServices().hasNext()) {
                                    addAgent(dfd1.getName());
                                    System.out.println("I, agent " + getAID().getName() + ", have found agent " + dfd1.getName().getName() + ".");
                                } else {
                                    removeAgent(dfd1.getName());
                                }
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

        _allAgents.add(getAID()); // add self

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

    private void addAgent(AID agent) {
        Platform.runLater(() -> {
            _otherAgents.add(agent.getName());
            Notifications.create()
                    .title("Agent Joined")
                    .text(agent.getName())
                    .darkStyle()
                    .showInformation();

        });

        _allAgents.add(agent);
    }

    private void removeAgent(AID agent) {
        Platform.runLater(() -> {
            _otherAgents.remove(agent.getName());
            Notifications.create()
                    .title("Agent Left")
                    .text(agent.getName())
                    .darkStyle()
                    .showWarning();

        });

        _allAgents.remove(agent);
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
