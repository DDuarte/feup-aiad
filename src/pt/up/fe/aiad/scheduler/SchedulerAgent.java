package pt.up.fe.aiad.scheduler;


import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import pt.up.fe.aiad.scheduler.agentbehaviours.ABTBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


public class SchedulerAgent extends Agent {
    public enum Type {
        ABT
    }

    public ArrayList<ScheduleEvent> _events; /** List of all the events this agent is participating in */
    public Map<String, ArrayList<AID>> _participants; /** For each of the events this agent is participating in, lists other participants */

    private final Type _agentType;
    public SchedulerAgent(Type agentType) {
        _agentType = agentType;
        _events = new ArrayList<ScheduleEvent>();
        _participants = new HashMap<String, ArrayList<AID>>();

    }

    @Override
    public void setup() {
        //TODO Create and show the GUI

        System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
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


        switch (_agentType) {
            case ABT:
                addBehaviour(new ABTBehaviour());
                break;
            default:
                System.err.print("Invalid type selected");
                doDelete();
                break;
        }
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
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }
}
