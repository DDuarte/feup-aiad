package pt.up.fe.aiad.tests;


import jade.core.AID;
import org.junit.Test;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.constraints.AfterHourConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduleEventTest {
    @Test
    public void testAfterHourConstraint() throws Exception {

        ScheduleEvent ev = new ScheduleEvent("Fazer AIAD", 4*30*60, null, null);

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 21, 0);

        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 31, 10, 0); //30/01/2004  18h30
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 14, 30);


        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c3, c4);

        ArrayList<TimeInterval> domain = new ArrayList<>();
        domain.add(ti1);

        ev.initialize(domain, null);

        assertEquals(ev._possibleSolutions.size(), 2);

        domain.add(ti2);
        ev.initialize(domain, null);
        assertEquals(ev._possibleSolutions.size(), 8);
    }
}
