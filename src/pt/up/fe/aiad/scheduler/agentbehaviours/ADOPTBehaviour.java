package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public class ADOPTBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;

    public HashMap<String, VirtualAgent> _virtualAgents = new HashMap<>();

    public static class Cost {
        public String sender;
        public HashMap<String, TimeInterval> context;
        public int lb;
        public int ub;
    }

    public static class VirtualAgent {

        ScheduleEvent _event;
        SchedulerAgent _masterAgent;
        private final ADOPTBehaviour _masterInstance;

        private TreeSet<String> _children = new TreeSet<>();
        private TreeSet<String> _pseudoChildren = new TreeSet<>();
        private TreeSet<String> _pseudoParents = new TreeSet<>();
        private String _parentX;
        private String _leader;
        private boolean _receivedTerminateFromParent = false;
        private boolean _isFinished = true;

        HashMap<String, VirtualAgent> _agents;
        int threshold;
        TimeInterval di;
        HashMap<String, TimeInterval> CurrentContext; // agent -> time
        HashMap<TimeInterval, HashMap<String, Integer>> lb;
        HashMap<TimeInterval, HashMap<String, Integer>> ub;
        HashMap<TimeInterval, HashMap<String, Integer>> t;
        HashMap<TimeInterval, HashMap<String, HashMap<String, TimeInterval>>> context; // D -> child -> context (agent -> timeInterval)

        public VirtualAgent(ScheduleEvent scheduleEvent, SchedulerAgent schedulerAgent, ADOPTBehaviour masterInstance,
                            DFSBehaviour.VirtualAgent va, String leader) {
            _event = scheduleEvent;
            _masterAgent = schedulerAgent;
            _masterInstance = masterInstance;
            _agents = _masterInstance._virtualAgents;

            _leader = leader;
            _parentX = va._parentX;
            _children = va._children;
            _pseudoChildren = va._pseudoChildren;
            _pseudoParents = va._pseudoParents;

            threshold = 0;
            CurrentContext = new HashMap<>();

            lb = new HashMap<>();
            ub = new HashMap<>();
            t = new HashMap<>();
            context = new HashMap<>();


            for (TimeInterval d : _event._possibleSolutions) {
                HashMap<String, Integer> childLbs = new HashMap<>();
                HashMap<String, Integer> childT = new HashMap<>();
                HashMap<String, Integer> childUbs = new HashMap<>();
                HashMap<String, HashMap<String, TimeInterval>> childContexts = new HashMap<>();
                for (String xl : _children) {
                    childLbs.put(xl, 0);
                    childT.put(xl, 0);
                    childUbs.put(xl, 10000);
                    childContexts.put(xl, new HashMap<>());
                }
                lb.put(d, childLbs);
                t.put(d, childT);
                ub.put(d, childUbs);
                context.put(d, childContexts);
            }

            di = _event._possibleSolutions.get(0);
            int currentDelta = delta(di);

            for (TimeInterval d : _event._possibleSolutions) {
                int tempDelta = delta(d);
                if (tempDelta < currentDelta) {
                    di = d;
                    currentDelta = tempDelta;
                }
                if (currentDelta == 0) {
                    break;
                }
            }

            backTrack();

            /*System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): leader: " + _leader);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): parent: " + _parentX);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): children: " +  Arrays.toString(_children.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pchildren: " +  Arrays.toString(_pseudoChildren.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pparent: " + Arrays.toString(_pseudoParents.toArray()));
            */
        }

        int delta(TimeInterval v) {
            int d = _event.getCost(v);

            for (Map.Entry<String, TimeInterval> xj : CurrentContext.entrySet()) {
                if (xj.getKey().split("-", 2)[1].equals(_event.getName()) && !xj.getValue().equals(v))
                    d += 1000;
            }

            for (Map.Entry<String, VirtualAgent> others : _agents.entrySet()) {
                if (others.getKey().compareTo(_event.getName()) < 0 && others.getValue().di != null &&
                        others.getValue().di.overlaps(v))
                    d += 1000;
            }

            return d;
        }

        int LB(TimeInterval v) {
            int c = delta(v);

            for (String xl : _children) {
                c += lb.get(v).get(xl);
            }
            return c;
        }

        int UB(TimeInterval v) {
            int c = delta(v);

            for (String xl : _children) {
                c += ub.get(v).get(xl);
            }
            return c;
        }

        int LB() {
            int totalLB = Integer.MAX_VALUE;
            for (TimeInterval d : _event._possibleSolutions) {
                int tempLB = LB(d);
                if (tempLB < totalLB)
                    totalLB = tempLB;
                if (totalLB == 0)
                    break;
            }
            return totalLB;
        }
        int UB() {
            int totalUB = Integer.MAX_VALUE;
            for (TimeInterval d : _event._possibleSolutions) {
                int tempUB = UB(d);
                if (tempUB < totalUB)
                    totalUB = tempUB;
                if (totalUB == 0)
                    break;
            }
            return totalUB;
        }

        void backTrack() {
            /*System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): leader: " + _leader);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): parent: " + _parentX);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): children: " +  Arrays.toString(_children.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pchildren: " +  Arrays.toString(_pseudoChildren.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pparent: " + Arrays.toString(_pseudoParents.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): my Di is : " + di.toString() + " with cost " + delta(di));
            */
            int tempUB = UB();
            if (threshold == tempUB) {
                chooseDiForMinUB();
            }
            else if (LB(di) > threshold) {
                chooseDiForMinLB();
            }
            //ENDIF
            //SEND (VALUE, (xi, di /* globals*/)) to each lower priority neighbor
            sendValue();
            maintainAllocationInvariant();

            if (threshold == tempUB) {
                if (_receivedTerminateFromParent || _leader.equals(_masterAgent.getName()+ "-" + _event.getName())) {
                    sendTerminate();
                    _isFinished = true;
                    _masterInstance.checkFinished();
                    return;
                }
            }
            sendCost();
        }

        private void chooseDiForMinUB() {
            int currentUB = UB(di);
            for (TimeInterval d : _event._possibleSolutions) {
                int tempUB = UB(d);
                if (tempUB < currentUB) {
                    currentUB = tempUB;
                    di = d;
                }
                if (currentUB == 0)
                    break;
            }
        }
        private void chooseDiForMinLB() {
            int currentLB = LB(di);
            for (TimeInterval d : _event._possibleSolutions) {
                int tempLB = LB(d);
                if (tempLB < currentLB) {
                    currentLB = tempLB;
                    di = d;
                }
                if (currentLB == 0)
                    break;
            }
        }

        private void maintainThresholdInvariant() {
            int lb = LB();
            int ub = UB();
            if (threshold < lb) {
                threshold = lb;
            }
            if (threshold > ub) {
                threshold = ub;
            }
        }

        private void maintainAllocationInvariant() {
            int deltaDi = delta(di);
            while (threshold > deltaDi + getSumOfChildrenThresholdsForDi()) {
                String xl = getFirstChildIf((x) -> ub.get(di).get(x) > t.get(di).get(x));
                t.get(di).put(xl, t.get(di).get(xl) + 1);
            }
            while (threshold < deltaDi + getSumOfChildrenThresholdsForDi()) {
                String xl = getFirstChildIf((x) -> t.get(di).get(x) > lb.get(di).get(x));
                t.get(di).put(xl, t.get(di).get(xl) - 1);
            }
            sendThreshold();
        }

        private void maintainChildThresholdInvariant() {
            for (TimeInterval d : _event._possibleSolutions) {
                for (String xl : _children) {
                    if (lb.get(d).get(xl) > t.get(d).get(xl)) {
                        t.get(d).put(xl, lb.get(d).get(xl));
                    }
                    if (t.get(d).get(xl) > ub.get(d).get(xl)) {
                        t.get(d).put(xl, ub.get(d).get(xl));
                    }
                }
            }
        }

        private String getFirstChildIf(Predicate<String> pred) {
            for (String x: _children) {
                if (pred.test(x))
                    return x;
            }
            return null;
        }

        private int getSumOfChildrenThresholdsForDi() {
            int c = 0;
            for (String xl : _children) {
                c += t.get(di).get(xl);
            }
            return c;
        }

        private void sendValue() {
            //TODO
            //  SEND (VALUE, (xi, di)) to each lower priority neighbor
        }

        private void sendTerminate() {
            //TODO
            // (48) SEND (TERMINATE,CurrentContext ∪ {(xidi)}) to each child
        }

        private void sendCost() {
            //  SEND (COST, xi, CurrentContext, LB,UB) to parent
            Cost cost = new Cost();
            cost.sender = _masterAgent.getName()+"-"+_event.getName();
            cost.context = CurrentContext;
            cost.lb = LB();
            cost.ub = UB();

            String json = Serializer.CostToJSON(cost);
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            String[] strs = _parentX.split("-", 2);
            msg.addReceiver(new AID(strs[0], true));
            msg.setContent("COST-" + strs[1] + "-" + json);
            msg.setConversationId("ADOPT");
            _masterAgent.send(msg);
        }

        private void sendThreshold() {
            //TODO
            //SEND (THRESHOLD, t(di, xl), CurrentContext ) to each child xl
        }

        public void receiveCost(Cost cost) {
            String thisName = _masterAgent.getName()+"-"+_event.getName();
            TimeInterval d = cost.context.get(thisName);
            cost.context.remove(thisName);
            if (!_receivedTerminateFromParent) {
                for (Map.Entry<String, TimeInterval> c : cost.context.entrySet()) {
                    if (!_parentX.equals(c.getKey()) && !_children.contains(c.getKey()) && !_pseudoChildren.contains(c.getKey()) && !_pseudoParents.contains(c.getKey())) {
                        CurrentContext.put(c.getKey(),c.getValue());
                    }
                }
                for (TimeInterval dPrime : _event._possibleSolutions) {
                    for (String xl : _children) {
                        if (!compatibleContexts(context.get(dPrime).get(xl), CurrentContext)) {
                            lb.get(dPrime).put(xl, 0);
                            t.get(dPrime).put(xl, 0);
                            ub.get(dPrime).put(xl, 10000);
                            context.get(dPrime).get(xl).clear();
                        }
                    }
                }
            }

            if (compatibleContexts(cost.context, CurrentContext)) {
                lb.get(d).put(cost.sender, cost.lb);
                ub.get(d).put(cost.sender, cost.ub);
                context.get(d).put(cost.sender, cost.context);
                maintainChildThresholdInvariant();
                maintainThresholdInvariant();
            }
            backTrack();
        }

        private boolean compatibleContexts(HashMap<String, TimeInterval> c1, HashMap<String, TimeInterval> c2) {
            Set<String> commonKeys = new TreeSet<>(c1.keySet());
            commonKeys.retainAll(c2.keySet());
            for (String xl : commonKeys) {
                if (!c1.get(xl).equals(c2.get(xl)))
                    return false;
            }
            return true;
        }
    }



    private String _leader;
    private HashMap<String, DFSBehaviour.VirtualAgent> _agentsDFS;

    public ADOPTBehaviour(String leader, HashMap<String, DFSBehaviour.VirtualAgent> agents) {
        _leader = leader;
        _agentsDFS = agents;
    }

    @Override
    public void onStart() {
        _agent = (SchedulerAgent) myAgent;

        if (_agent._events.isEmpty()) {
            allFinished = true;
            _agent.finishedAlgorithm();
            return;
        }

        TreeSet<String> orderedEventNames = new TreeSet<>(_agentsDFS.keySet()); //because delta depends on overlapping events this order is necessary the first time
        for (String ev : orderedEventNames) {
            DFSBehaviour.VirtualAgent ag = _agentsDFS.get(ev);
            _virtualAgents.put(ev.split("-",2)[1], new VirtualAgent(ag._event, _agent,
                    this, ag, _leader));
        }


    }

    @Override
    public void action() {
        if (allFinished)
            return;

        MessageTemplate mt = MessageTemplate.MatchConversationId("ADOPT");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            int separatorIndex = msg.getContent().indexOf('-');
            if (separatorIndex != -1) {
                String str = msg.getContent();
                String[] strs = str.split("-", 3);
                switch (strs[0]) {
                    case "COST":
                        _virtualAgents.get(strs[1]).receiveCost(Serializer.CostFromJSON(strs[2]));
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

    public void checkFinished() {
        allFinished = true;
        for (VirtualAgent va : _virtualAgents.values()) {
            allFinished = allFinished && va._isFinished;
            if (!allFinished)
                return;
        }
    }
}
