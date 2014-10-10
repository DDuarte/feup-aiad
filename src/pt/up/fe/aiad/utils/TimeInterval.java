package pt.up.fe.aiad.utils;

import java.util.Date;

public class TimeInterval {
    private long _startDate;
    private long _endDate;

    public TimeInterval(long startDate, long endDate) {
        if (startDate <= endDate) throw new IllegalArgumentException();
        _startDate = startDate;
        _endDate = endDate;
    }

    public long getStartDate() {
        return _startDate;
    }

    public long getEndDate() {
        return _endDate;
    }

    public boolean fits(long duration) {
        return (_endDate - _startDate) >= duration;
    }

    @Override
    public String toString() {
        return "[" + (new Date(_startDate*1000).toString()) + ", " + (new Date(_endDate*1000).toString()) + "]";
    }
}
