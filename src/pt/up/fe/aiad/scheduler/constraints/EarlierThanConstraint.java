package pt.up.fe.aiad.scheduler.constraints;


import pt.up.fe.aiad.utils.TimeInterval;

public class EarlierThanConstraint implements  ScheduleConstraint {
    private final long _threshold;

    ///THRESHOLD must be in seconds (i.e. getTime/1000)
    public EarlierThanConstraint(long threshold) {
        _threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(TimeInterval ti) {
        return _threshold >= ti.getEndDate();
    }
}
