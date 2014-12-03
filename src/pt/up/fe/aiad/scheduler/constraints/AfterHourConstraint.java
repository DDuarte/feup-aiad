package pt.up.fe.aiad.scheduler.constraints;


import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

public class AfterHourConstraint implements ScheduleConstraint{
    private final int _hour;
    private final int _minute;

    public AfterHourConstraint(Calendar thresholdPeriod) {
        _hour = thresholdPeriod.get(Calendar.HOUR_OF_DAY);
        _minute = thresholdPeriod.get(Calendar.MINUTE);
    }

    @Override
    public boolean isSatisfiedBy(TimeInterval ti) {
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(ti.getStartDate()*1000);
        return (_hour < temp.get(Calendar.HOUR_OF_DAY) || (_hour == temp.get(Calendar.HOUR_OF_DAY) && _minute <= temp.get(Calendar.MINUTE)));
    }
}
