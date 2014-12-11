package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.scheduler.Statistics;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ABTBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;

    private HashMap<String, VirtualAgent> _agents = new HashMap<>();

    public static class VirtualAgent {
        private ABTSelf self = new ABTSelf();
        private ScheduleEvent _event;
        private SchedulerAgent _masterAgent;
        private boolean isFinished = false;
        private ABTBehaviour _masterInstance;

        public Statistics Stats = new Statistics();

        public VirtualAgent(ScheduleEvent event, SchedulerAgent agent, ABTBehaviour masterInstance) {
            _masterInstance = masterInstance;
            _event = event;
            _masterAgent = agent;

            Stats.setVariableName(_masterAgent.getLocalName() + "-" + _event.getName());

            self.x = new Variable();
            self.x.agent = _masterAgent.getAID().getName();
            self.x.v = null;

            self.nogoods = new ArrayList<>();
            self.lower_agents = new TreeSet<>();
            self.agentview = new HashMap<>();
            self.tag = new TreeSet<>();
            self.cost = 0;
            self.exact = true;

            self.domain = _event._possibleSolutions;

            for (AID a : _event._participants) {
                if (_masterAgent.getAID().compareTo(a) < 0)
                    self.lower_agents.add(a.getName());

                // if (_agent.getAID().compareTo(agent) > 0)
                // )    self.agentview.put(agent.getName(), null);
            }

            adjust_value();
        }

        public void adjust_value() {

            TimeInterval old_value = self.x.v;
            self.cost = Integer.MAX_VALUE;

            for (TimeInterval v : self.domain) {
                int delta = _event.getCost(v);
                int LB = 0;
                TreeSet<String> tag = new TreeSet<>();
                tag.add(_masterAgent.getName());
                boolean exact = true;

                for (String xj : self.agentview.keySet()) {
                    if (!self.agentview.get(xj).equals(v))
                        delta += 1000;
                }

                for (Map.Entry<String, VirtualAgent> others : _masterInstance._agents.entrySet()) {
                    if (others.getKey().compareTo(_event.getName()) < 0 && others.getValue().self.x.v != null &&
                            others.getValue().self.x.v.overlaps(v))
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
                if (_masterAgent.getAID().getName().equals(_event._participants.first().getName())) {
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
            _event._currentInterval = self.x.v;
            _event._currentCost = cost;

            if (!self.lower_agents.isEmpty())
                sendTerminate(cost);

            isFinished = true;

            _masterInstance.checkFinished();
            if (_masterInstance.allFinished)
                _masterAgent.finishedAlgorithm();
        }

        private void sendNoGood(String agent, NoGood ng) {
            String json = Serializer.NoGoodToJSON(ng);

            ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
            msg.addReceiver(new AID(agent, true));
            msg.setContent("NOGOOD-" + _event.getName() + "-" + json);
            msg.setConversationId("ABT");
            _masterAgent.send(msg);

            Stats.sentMessage("NOGOOD");
        }

        private void sendOk(String agent, Variable x) {
            String json = Serializer.VariableToJSON(x);

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(new AID(agent, true));
            msg.setContent("OK?-" + _event.getName() + "-" + json);
            msg.setConversationId("ABT");
            _masterAgent.send(msg);

            Stats.sentMessage("OK?");
        }

        private void sendAddLink(String agent, Variable x) {
            String json = Serializer.VariableToJSON(x);

            ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
            msg.addReceiver(new AID(agent, true));
            msg.setContent("LINK-" + _event.getName() + "-" + json);
            msg.setConversationId("ABT");
            _masterAgent.send(msg);

            Stats.sentMessage("LINK");
        }

        private void sendTerminate(int cost) {
            ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
            for (String agent : self.lower_agents) {
                msg.addReceiver(new AID(agent, true));
                Stats.sentMessage("TERM");
            }
            msg.setContent("TERM-" + _event.getName() + "-" + cost);
            msg.setConversationId("ABT");
            _masterAgent.send(msg);
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

    public void checkFinished() {
        allFinished = true;
        for (VirtualAgent va : _agents.values())
            allFinished = allFinished && va.isFinished;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;
        if (_agent._events.isEmpty()) {
            allFinished = true;
            _agent.finishedAlgorithm();
            return;
        }

        for (ScheduleEvent ev : _agent._events) {
            _agents.put(ev.getName(), new VirtualAgent(ev, _agent, this));
        }
    }

    @Override
    public void action() {
        if (allFinished)
            return;

        MessageTemplate mt = MessageTemplate.MatchConversationId("ABT");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 3);
                switch (strs[0]) {
                    case "OK?":
                        _agents.get(strs[1]).receiveOk(Serializer.VariableFromJSON(strs[2]));
                        _agents.get(strs[1]).Stats.receivedMessage("OK?");
                        break;
                    case "NOGOOD":
                        _agents.get(strs[1]).receiveNoGood(Serializer.NoGoodFromJSON(strs[2]));
                        _agents.get(strs[1]).Stats.receivedMessage("NOGOOD");
                        break;
                    case "LINK":
                        _agents.get(strs[1]).receiveAddLink(Serializer.VariableFromJSON(strs[2]));
                        _agents.get(strs[1]).Stats.receivedMessage("LINK");
                        break;
                    case "TERM":
                        _agents.get(strs[1]).receiveTerminate(Integer.parseInt(strs[2]));
                        _agents.get(strs[1]).Stats.receivedMessage("TERM");
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
        return allFinished;
    }
}
