package pt.up.fe.aiad.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Represent an interval between two dates
 */
public class TimeInterval implements Comparable<TimeInterval> {
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


    public boolean overlaps(TimeInterval other) {
        if (other.getStartDate() >= getStartDate() && other.getStartDate() < getEndDate())
            return true;
        return (other.getEndDate() > getStartDate() && other.getEndDate() <= getEndDate());
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return "[" + (dateFormat.format(sd.getTime())) + ", " + (dateFormat.format(ed.getTime())) + "]";
        }
    }

    @Override
    public int compareTo(TimeInterval o) { //only compares start date

        Long sd = _startDate;
        Long otherSd = o.getStartDate();
        return sd.compareTo(otherSd);
    }

    public TimeInterval getNext() {
        final int INTERVAL_SLOT = 30 * 60; // 30 minutes

        return new TimeInterval(_startDate + INTERVAL_SLOT, _endDate + INTERVAL_SLOT);
    }

    public static Calendar calendarFromLocalDate(LocalDate lc, int hours, int minutes) {
        if (lc == null)
            return null;

        Calendar c = Calendar.getInstance();
        c.set(lc.getYear(), lc.getMonthValue()-1, lc.getDayOfMonth(), hours, minutes, 0);
        return c;
    }

    public static ArrayList<TimeInterval> getPossibleIntervals(long startDate, long endDate, int duration) {
        ArrayList<TimeInterval> intervals = new ArrayList<>();

        while (startDate < endDate) {
            intervals.add(new TimeInterval(startDate, startDate + duration));
            startDate += duration;
        }

        return intervals;
    }
}
