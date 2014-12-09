package pt.up.fe.aiad.scheduler.agentbehaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ADOPTBehaviour extends SimpleBehaviour {

    private boolean allFinished = false;
    private SchedulerAgent _agent;

    public HashMap<String, VirtualAgent> _virtualAgents = new HashMap<>();

    public static class VirtualAgent {

        ScheduleEvent _event;
        SchedulerAgent _masterAgent;
        private final ADOPTBehaviour _masterInstance;

        private TreeSet<String> _children = new TreeSet<>();
        private TreeSet<String> _pseudoChildren = new TreeSet<>();
        private TreeSet<String> _pseudoParents = new TreeSet<>();
        private String _parentX;
        private String _leader;

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
                    childUbs.put(xl, Integer.MAX_VALUE);
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
                if (!xj.getValue().equals(v))
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

        void backTrack() {
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): leader: " + _leader);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): parent: " + _parentX);
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): children: " +  Arrays.toString(_children.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pchildren: " +  Arrays.toString(_pseudoChildren.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): pparent: " + Arrays.toString(_pseudoParents.toArray()));
            System.err.println("ADOPT(" + _masterAgent.getLocalName() + "-" + _event.getName() + "): my Di is : " + di.toString() + " with cost " + delta(di));

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
                String[] strs = str.split("-", 3 /*?*/);
                switch (strs[0]) {
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
