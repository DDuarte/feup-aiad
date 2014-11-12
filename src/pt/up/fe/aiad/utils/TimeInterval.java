package pt.up.fe.aiad.utils;

import java.util.Date;

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
    public TimeInterval(Date startDate, Date endDate) {
        if (startDate.getTime() <= endDate.getTime()) throw new IllegalArgumentException();
        _startDate = startDate.getTime();
        _endDate = endDate.getTime();
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
        return "[" + (new Date(_startDate*1000).toString()) + ", " + (new Date(_endDate*1000).toString()) + "]";
    }
}
