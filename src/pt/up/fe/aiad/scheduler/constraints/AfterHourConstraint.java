package pt.up.fe.aiad.scheduler.constraints;


import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

public class AfterHourConstraint implements ScheduleConstraint{
    private final int _hour;
    private final int _minute;

    public AfterHourConstraint(int hour, int minute) {
        _hour = hour;
        _minute = minute;
    }

    @Override
    public boolean isSatisfiedBy(TimeInterval ti) {
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(ti.getStartDate()*1000);
        return (_hour < temp.get(Calendar.HOUR_OF_DAY) || (_hour == temp.get(Calendar.HOUR_OF_DAY) && _minute <= temp.get(Calendar.MINUTE)));
    }

    @Override
    public String toString() {
        return "Event must take place after " + Integer.toString(_hour) + "h" + Integer.toString(_minute);
    }
}
