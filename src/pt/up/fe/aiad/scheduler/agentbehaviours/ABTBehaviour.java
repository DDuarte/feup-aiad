package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.*;

public class ABTBehaviour extends SimpleBehaviour {
    //TODO private TreeSet<AID> _links;
    //TODO: create agent view and link structure

    private boolean isFinished = false;
    private SchedulerAgent _agent;

    private ABTSelf self = new ABTSelf();

    public ABTBehaviour () {

    }

    public static class ABTSelf {
        Variable x;
        ArrayList<TimeInterval> domain;
        HashMap<String, TimeInterval> agentview;
        TreeSet<String> lower_agents;
        ArrayList<NoGood> nogoods;
        int cost;
        TreeSet<String> tag;
        boolean exact;
    }

    public static class Variable {
        public TimeInterval v;
        public String agent;
    }

    public static class NoGood {
        public TimeInterval v;
        public HashMap<String, TimeInterval> cond;
        public TreeSet<String> tag;
        public int cost;
        public boolean exact;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
        if (_agent._events.isEmpty()) {
            terminate(0);
            return;
        }

        self.x = new Variable();
        self.x.agent = myAgent.getAID().getName();
        self.x.v = null;

        self.nogoods = new ArrayList<>();
        self.lower_agents = new TreeSet<>();
        self.agentview = new HashMap<>();
        self.tag = new TreeSet<>();
        self.cost = 0;
        self.exact = true;

        self.domain = _agent._events.get(0)._possibleSolutions;

        for (AID agent : _agent._events.get(0)._participants) {
            if (_agent.getAID().compareTo(agent) < 0)
                self.lower_agents.add(agent.getName());

            // if (_agent.getAID().compareTo(agent) > 0)
            // )    self.agentview.put(agent.getName(), null);
        }

        adjust_value();
    }

    public void adjust_value() {

        TimeInterval old_value = self.x.v;
        self.cost = Integer.MAX_VALUE;

        for (TimeInterval v : self.domain) {
            int delta = _agent._events.get(0).getCost(v);
            int LB = 0;
            TreeSet<String> tag = new TreeSet<>();
            tag.add(_agent.getName());
            boolean exact = true;

            for (String xj : self.agentview.keySet()) {
                if (!self.agentview.get(xj).equals(v))
                    delta += 1000;
            }

            for (NoGood ng : self.nogoods) {
                if (!ng.v.equals(v)) {
                    continue;
                }

                LB += ng.cost;
                tag.addAll(ng.tag);
                exact = exact && ng.exact;
            }

            exact = exact && tag.containsAll(self.lower_agents);
            if (delta + LB <= self.cost) {
                self.x.v = v;
                self.cost = delta + LB;
                self.tag = tag;
                self.exact = exact;
            }
        }

        if (self.cost != 0 || self.exact) {
            if (_agent.getName().equals(_agent.allAgents.first().getName())) {
                terminate(self.cost);
                return;
            }

            if (!self.agentview.isEmpty()) {
                TreeSet<String> s = new TreeSet<>(self.agentview.keySet());
                Variable xj = new Variable();
                xj.agent = s.last();
                xj.v = self.agentview.get(xj.agent);
                NoGood ng = new NoGood();
                ng.v = xj.v;
                ng.cond = new HashMap<>(self.agentview);
                ng.cond.remove(xj.agent);
                ng.tag = self.tag;
                ng.exact = self.exact;
                ng.cost = self.cost;
                sendNoGood(xj.agent, ng);
            }
        }

        if (!self.x.v.equals(old_value)) {
            for (String a : self.lower_agents) {
                sendOk(a, self.x);
            }
        }
    }

    private void terminate(int cost) {
        _agent._events.get(0)._currentInterval = self.x.v;
        _agent._events.get(0)._currentCost = cost;

        if (!self.lower_agents.isEmpty())
            sendTerminate(cost);

        isFinished = true;
        _agent.finishedAlgorithm();
    }

    private void sendNoGood(String agent, NoGood ng) {
        String json = Serializer.NoGoodToJSON(ng);

        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.addReceiver(new AID(agent, true));
        msg.setContent("NOGOOD-" + json);
        msg.setConversationId("ABT");
        getAgent().send(msg);
    }

    private void sendOk(String agent, Variable x) {
        String json = Serializer.VariableToJSON(x);

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.addReceiver(new AID(agent, true));
        msg.setContent("OK?-" + json);
        msg.setConversationId("ABT");
        getAgent().send(msg);
    }

    private void sendAddLink(String agent, Variable x) {
        String json = Serializer.VariableToJSON(x);

        ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
        msg.addReceiver(new AID(agent, true));
        msg.setContent("LINK-" + json);
        msg.setConversationId("ABT");
        getAgent().send(msg);
    }

    private void sendTerminate(int cost) {
        ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
        for (String agent : self.lower_agents) {
            msg.addReceiver(new AID(agent, true));
        }
        msg.setContent("TERM-" + cost);
        msg.setConversationId("ABT");
        getAgent().send(msg);
    }

    private void receiveAddLink(Variable xj) {
        self.lower_agents.add(xj.agent);
        sendOk(xj.agent, self.x);
    }

    private void receiveOk(Variable var) {
        ArrayList<NoGood> toRemove = new ArrayList<>();
        for (NoGood ng : self.nogoods) {
            for (Map.Entry<String, TimeInterval> vc : ng.cond.entrySet()) {
                if (vc.getKey().equals(var.agent) && !vc.getValue().equals(var.v)) {
                    toRemove.add(ng);
                    break;
                }
            }
        }

        toRemove.forEach(self.nogoods::remove);

        self.agentview.put(var.agent, var.v);
        adjust_value();
    }

    private void receiveNoGood(NoGood new_ng) {
        for (Map.Entry<String, TimeInterval> var : new_ng.cond.entrySet()) {
            TimeInterval cvv = self.agentview.get(var.getKey());
            if (cvv == null) {
                self.agentview.put(var.getKey(), var.getValue());
                sendAddLink(var.getKey(), self.x);
            } else {
                if (!cvv.equals(var.getValue()))
                    return;
            }
        }

        ArrayList<NoGood> toRemove = new ArrayList<>();
        for (NoGood ng : self.nogoods) {
            if (ng.v.equals(new_ng.v) && new_ng.tag.containsAll(ng.tag)) {
                toRemove.add(ng);
            }
        }

        toRemove.forEach(self.nogoods::remove);

        self.nogoods.add(new_ng); /* can this be duplicated? */
        adjust_value();
    }

    private void receiveTerminate(int cost) {
        terminate(cost);
    }

    @Override
    public void action() {
        if (isFinished)
            return;

        MessageTemplate mt = MessageTemplate.MatchConversationId("ABT");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 2);
                switch (strs[0]) {
                    case "OK?":
                        receiveOk(Serializer.VariableFromJSON(strs[1]));
                        break;
                    case "NOGOOD":
                        receiveNoGood(Serializer.NoGoodFromJSON(strs[1]));
                        break;
                    case "LINK":
                        receiveAddLink(Serializer.VariableFromJSON(strs[1]));
                        break;
                    case "TERM":
                        receiveTerminate(Integer.parseInt(strs[1]));
                        break;
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
        return isFinished;
    }
}
