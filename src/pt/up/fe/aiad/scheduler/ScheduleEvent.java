package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import javafx.application.Platform;
import pt.up.fe.aiad.scheduler.constraints.ScheduleConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;

public class ScheduleEvent {
    private String _name;
    private long _duration; /**< duration in seconds */
    public ArrayList<AID> _participants;
    private ArrayList<TimeInterval> _availableIntervals; // Domain
    private ArrayList<ScheduleConstraint> _constraints;

    private boolean _initialized = false;

    public TimeInterval maxBounds;

    public ScheduleEvent(String name, long duration, ArrayList<AID> participants, TimeInterval maxInterval) {
        _name = name;
        _duration = duration;
        _participants = participants;

        Platform.runLater(() -> {
            System.out.println("Created event " + name + " with interval: " + maxInterval.toString(false) + ". Inviting:");
            for (AID a : participants) {
                System.out.println(a.getName());
            }
        });


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

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }
}
