package pt.up.fe.aiad.scheduler.constraints;

import pt.up.fe.aiad.utils.TimeInterval;

public interface ScheduleConstraint {
    public boolean isSatisfiedBy(TimeInterval ti);

    @Override
    public String toString();
}
