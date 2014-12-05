package pt.up.fe.aiad.scheduler.constraints;


import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

public class EarlierThanConstraint implements  ScheduleConstraint {
    private final long _threshold;

    // THRESHOLD must be in seconds (i.e. getTime/1000)
    public EarlierThanConstraint(long threshold) {
        _threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(TimeInterval ti) {
        return _threshold >= ti.getEndDate();
    }

    @Override
    public String toString() {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(_threshold*1000);
        return "Event should take place before " + c1.getTime().toString();
    }
}
