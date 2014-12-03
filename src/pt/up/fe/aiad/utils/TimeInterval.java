package pt.up.fe.aiad.utils;

import java.util.Calendar;

/**
 * Represent an interval between two dates
 */
public class TimeInterval {
    private long _startDate;
    private long _endDate;

    /**
     * Constructor
     * @param startDate Starting date of the interval in seconds since Unix Epoch
     * @param endDate Ending date of the interval in seconds since Unix Epoch
     */
    public TimeInterval(long startDate, long endDate) {
        if (startDate <= endDate) throw new IllegalArgumentException();
        _startDate = startDate;
        _endDate = endDate;
    }

    /**
     * Constructor
     * @param startDate Starting date of the interval
     * @param endDate Ending date of the interval
     */
    public TimeInterval(Calendar startDate, Calendar endDate) {
        if (startDate.getTimeInMillis() > endDate.getTimeInMillis())
            throw new IllegalArgumentException();
        _startDate = startDate.getTimeInMillis() / 1000;
        _endDate = endDate.getTimeInMillis() / 1000;
    }

    public TimeInterval(String string) {
        String[] parts = string.split(",");

        if (parts.length != 2)
            throw new IllegalArgumentException();

        _startDate = Long.parseLong(parts[0]);
        _endDate = Long.parseLong(parts[1]);
    }

    /**
     * @return Starting date of the interval in seconds since Unix Epoch
     */
    public long getStartDate() {
        return _startDate;
    }

    /**
     * @return Ending date of the interval in seconds since Unix Epoch
     */
    public long getEndDate() {
        return _endDate;
    }

    /**
     * @return Duration of the interval in seconds since Unix Epoch
     */
    public long getDuration() {
        return _endDate - _startDate;
    }

    /**
     * @param duration Duration of the event in seconds since Unix Epoch
     * @return True if the duration of the event
     */
    public boolean fits(long duration) {
        return getDuration() >= duration;
    }

    /**
     * @return String representation of the interval
     */
    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean simple) {
        Calendar sd = Calendar.getInstance();
        Calendar ed = Calendar.getInstance();
        sd.setTimeInMillis(_startDate*1000);
        ed.setTimeInMillis(_endDate*1000);
        if (simple) {
            return _startDate + "," + _endDate;
        } else {
            return "[" + (sd.getTime().toString()) + ", " + (sd.getTime().toString()) + "]";
        }
    }
}
