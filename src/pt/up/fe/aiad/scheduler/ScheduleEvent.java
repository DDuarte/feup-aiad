package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import pt.up.fe.aiad.scheduler.constraints.ScheduleConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class ScheduleEvent {
    private String _name;
    private long _duration; /**< duration in seconds */
    public TreeSet<AID> _participants;
    public ArrayList<TimeInterval> _availableIntervals; // Domain
    public ArrayList<ScheduleConstraint> _constraints;
    public ArrayList<TimeInterval> _possibleSolutions;

    public TimeInterval _currentInterval; //null if no solution is found
    public int _currentCost;

    private boolean _initialized = false;

    public TimeInterval maxBounds;

    public ScheduleEvent(String name, long duration, TreeSet<AID> participants, TimeInterval maxInterval) {
        _name = name;
        _duration = duration;
        _participants = participants;

        /*
        Platform.runLater(() -> {
            System.out.println("Created event " + name + " with interval: " + maxInterval.toString(false) + ". Inviting:");
            for (AID a : participants) {
                System.out.println(a.getName());
            }
        });*/

        maxBounds = maxInterval;
    }

    public void initialize(ArrayList<TimeInterval> domain, ArrayList<ScheduleConstraint> constraints) {
        _availableIntervals = domain;
        _constraints = constraints;

        initializePossibilities(domain);
        _initialized = true;
    }

    private void initializePossibilities(ArrayList<TimeInterval> domain) {
        _possibleSolutions = new ArrayList<>();
        for (TimeInterval ti : domain) {
            TimeInterval possibility = new TimeInterval(ti.getStartDate(), ti.getStartDate() + _duration);
            while (ti.contains(possibility)) {
                _possibleSolutions.add(possibility);
                possibility = possibility.getNext();
            }
        }
        Collections.reverse(_possibleSolutions);
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public long getDuration() {
        return _duration;
    }

    public TimeInterval getMaxBounds() {
        return maxBounds;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    public int getCost(TimeInterval interval) {
        int cost = 0;
        final int CONSTRAINT_COST = 1;

        for (ScheduleConstraint c : _constraints) {
            if (!c.isSatisfiedBy(interval)) {
                cost += CONSTRAINT_COST;
            }
        }

        return cost;
    }
}
