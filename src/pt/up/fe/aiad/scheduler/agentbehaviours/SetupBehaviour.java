package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.scheduler.Serializer;

public class SetupBehaviour extends CyclicBehaviour {

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String msgType = msg.getContent().substring(0, msg.getContent().indexOf('-'));
                if (msgType.equals("INVITATION")) {
                    RegisterInvitation(msg);
                }
                else if (msgType.equals("LEAVING")) {
                    RegisterLeavingEvent(msg);
                }
                else if (msgType.equals("ADDING")) {
                    RegisterEventAddition(msg);
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

    private void RegisterEventAddition(ACLMessage msg) {
        String msgText = msg.getContent().substring(msg.getContent().indexOf('-')+1, msg.getContent().length());
        String eventName = msgText.substring(0, msgText.indexOf(','));
        String agentName  = msgText.substring(msgText.indexOf(',') + 1, msgText.length());
        if (!msg.getSender().getName().equals(myAgent.getName())) {
            boolean foundEv = false;
            for (ScheduleEvent ev : ((SchedulerAgent) myAgent)._events) {
                if (ev.getName().equals(eventName)) {
                    ev._participants.add(((SchedulerAgent) myAgent).agentNameToAid.get(agentName));
                    foundEv = true;
                    break;
                }
            }
            if (!foundEv) {
                for (ScheduleEvent ev : ((SchedulerAgent) myAgent)._invitedTo) {
                    if (ev.getName().equals(eventName)) {
                        ev._participants.add(((SchedulerAgent) myAgent).agentNameToAid.get(agentName));
                        foundEv = true;
                        break;
                    }
                }
            }
            if (foundEv) {
                Platform.runLater(() -> {
                    Notifications.create()
                            .title("Agent Joined Event")
                            .text(((SchedulerAgent) myAgent).agentNameToAid.get(agentName).getLocalName() + " has been invited to event " + eventName)
                            .darkStyle()
                            .showInformation();
                });
            }
        }
        else {
            Platform.runLater(() -> {
                Notifications.create()
                        .title("Agent Joined Event")
                        .text(((SchedulerAgent) myAgent).agentNameToAid.get(agentName).getLocalName() + " has been invited to event " + eventName)
                        .darkStyle()
                        .showInformation();
            });
        }
    }

    private void RegisterLeavingEvent(ACLMessage msg) {
        String eventName = msg.getContent().substring(msg.getContent().indexOf('-')+1, msg.getContent().length());
        boolean foundEv = false;
        for (ScheduleEvent ev : ((SchedulerAgent)myAgent)._events) {
            if (ev.getName().equals(eventName)) {
                ev._participants.remove(msg.getSender());
                foundEv = true;
                break;
            }
        }
        if (!foundEv) {
            for (ScheduleEvent ev : ((SchedulerAgent) myAgent)._invitedTo) {
                if (ev.getName().equals(eventName)) {
                    ev._participants.remove(msg.getSender());
                    foundEv = true;
                    break;
                }
            }
        }
        if (foundEv) {
            Platform.runLater(() -> {
                Notifications.create()
                        .title("Agent Left Event")
                        .text(msg.getSender().getLocalName() + " has left the event " + eventName)
                        .darkStyle()
                        .showWarning();
            });
        }
    }

    private void RegisterInvitation(ACLMessage msg) {
        Platform.runLater(() -> {
            String jsonContent = msg.getContent().substring(msg.getContent().indexOf('-') + 1, msg.getContent().length());
            ScheduleEvent newEv = Serializer.EventProposalFromJSON(jsonContent);
            Notifications.create()
                    .title("Invited to Event")
                    .text(msg.getSender().getLocalName() + " invited you to " + newEv.getName())
                    .darkStyle()
                    .showInformation();
            ((SchedulerAgent) myAgent)._invitedTo.add(newEv);
        });
    }

}
