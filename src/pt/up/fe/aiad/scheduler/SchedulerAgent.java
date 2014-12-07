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
import pt.up.fe.aiad.scheduler.agentbehaviours.SetupBehaviour;

import java.util.*;


public class SchedulerAgent extends Agent {
    public enum Type {
        ABT
    }

    /**
     * List of all the events this agent is participating in
     */
    public ObservableList<ScheduleEvent> _events = FXCollections.observableArrayList();

    /**
     * List of all the events this agent has been invited to attend
     */
    public ObservableList<ScheduleEvent> _invitedTo = FXCollections.observableArrayList();

    /**
     * All agent names excluding self
     */
    public ObservableList<String> otherAgents = FXCollections.observableArrayList();
    public Set<AID> allAgents = new TreeSet<>();
    public HashMap<String, AID> agentNameToAid = new HashMap<>();

    private Type _agentType;

    public static void main(String[] args) {
        if (args.length == 0) {
            String[] ar = {"-container"};
            jade.Boot.main(ar);
        } else
            jade.Boot.main(args);
    }

    public SchedulerAgent(Type agentType) {
        _agentType = agentType;
    }

    @Override
    public void setup() {
        try {
            Platform.runLater(() -> System.out.println("Hello. I, agent " + getAID().getName() + " am alive now."));
        } catch (Exception e) {
            System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
        }
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
                                    try {
                                        Platform.runLater(() -> System.out.println("I, agent " + getAID().getName() + ", have found agent " + dfd1.getName().getName() + "."));
                                    } catch (Exception e) {
                                        System.out.println("I, agent " + getAID().getName() + ", have found agent " + dfd1.getName().getName() + ".");
                                    }

                                } else {
                                    removeAgent(dfd1.getName());
                                }
                            }
                        }
                    } catch (FIPAException fe) {
                        Platform.runLater(fe::printStackTrace);
                    }
                }
            });

            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            Platform.runLater(fe::printStackTrace);
        }

        allAgents.add(getAID()); // add self

        addBehaviour(new SetupBehaviour());

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

    public void dispatchInvitations(ScheduleEvent ev) {
        Platform.runLater(() -> _invitedTo.add(ev));

        if (ev._participants.size() > 1) {
            String json = Serializer.EventProposalToJSON(ev);

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            ev._participants.stream().filter(a -> !a.getName().equals(getAID().getName())).forEach(msg::addReceiver);

            msg.setContent("INVITATION-" + json);
            msg.setConversationId("schedule-align");
            send(msg);
        }
    }

    public void acceptInvitation(ScheduleEvent ev) {
        if (_invitedTo.contains(ev)) {
            Platform.runLater(() -> {
                _invitedTo.remove(ev);
                _events.add(ev);
            });
        }
    }

    public void rejectInvitation(ScheduleEvent ev) {
        Platform.runLater(() -> _invitedTo.remove(ev));
        ev._participants.remove(getAID());
        if (ev._participants.size() > 0) {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            ev._participants.forEach(msg::addReceiver);

            msg.setContent("LEAVING-" + ev.getName());
            msg.setConversationId("schedule-align");
            send(msg);
        }
    }

    public void leaveEvent(ScheduleEvent ev) {
        Platform.runLater(() -> _events.remove(ev));
        ev._participants.remove(getAID());
        if (ev._participants.size() > 0) {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            ev._participants.forEach(msg::addReceiver);

            msg.setContent("LEAVING-" + ev.getName());
            msg.setConversationId("schedule-align");
            send(msg);
        }
    }

    private void addAgent(AID agent) {
        Platform.runLater(() -> {
            otherAgents.add(agent.getName());
            Notifications.create()
                    .title("Agent Joined")
                    .text(agent.getName())
                    .darkStyle()
                    .showInformation();

        });

        allAgents.add(agent);
        agentNameToAid.put(agent.getName(), agent);
    }


    private void removeAgent(AID agent) {
        Platform.runLater(() -> {
            otherAgents.remove(agent.getName());
            Notifications.create()
                    .title("Agent Left")
                    .text(agent.getName())
                    .darkStyle()
                    .showWarning();

        });

        allAgents.remove(agent);
        agentNameToAid.remove(agent.getName());
        for (ScheduleEvent e : _events) {
            e._participants.remove(agent);
        }
        for (ScheduleEvent e : _invitedTo) {
            e._participants.remove(agent);
        }
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            Platform.runLater(fe::printStackTrace);
        }
        //TODO Close the GUI if necessary

        // Printout a dismissal message
        try {
            Platform.runLater(() -> System.out.println("Agent " + getAID().getName() + " terminating."));
        } catch (Exception e) {
            System.out.println("Agent " + getAID().getName() + " terminating.");
        }
    }
}
