package pt.up.fe.aiad.scheduler;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class SchedulerAgent extends Agent {

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
