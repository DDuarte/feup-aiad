package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;

public class ScheduleEvent {
    private String _name;
    private final long _duration;
    private ArrayList<AID> _participants;
    private ArrayList<TimeInterval> _availableIntervals; //Domain

    public ScheduleEvent(String name, long duration) {
        _name = name;
        _duration = duration;
        _participants = new ArrayList<AID>();
        _availableIntervals = new ArrayList<TimeInterval>();
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }
}
