package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import pt.up.fe.aiad.scheduler.constraints.ScheduleConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;

public class ScheduleEvent {
    private String _name;
    private long _duration; /**< duration in seconds */
    public ArrayList<AID> _participants;
    public ArrayList<TimeInterval> _availableIntervals; // Domain
    public ArrayList<ScheduleConstraint> _constraints;

    public TimeInterval _currentInterval; //null if no solution is found

    private boolean _initialized = false;

    public TimeInterval maxBounds;

    public ScheduleEvent(String name, long duration, ArrayList<AID> participants, TimeInterval maxInterval) {
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

    public ScheduleEvent(String name, long duration, ArrayList<AID> participants, ArrayList<TimeInterval> domain, ArrayList<ScheduleConstraint> constraints) {
        _name = name;
        _duration = duration;
        _participants = participants;
        _availableIntervals = domain;
        _constraints = constraints;
        _initialized = true;
    }

    public void initialize(ArrayList<TimeInterval> domain, ArrayList<ScheduleConstraint> constraints) {
        _availableIntervals = domain;
        _constraints = constraints;
        _initialized = true;
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
