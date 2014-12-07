package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.constraints.*;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintTest {

    @Test
    public void testAfterHourConstraint() throws Exception {

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 25, 18, 35);
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 18, 0);
        Calendar c5 = Calendar.getInstance(); c5.set(2004, Calendar.JANUARY, 31, 15, 40);


        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c2, c4);
        TimeInterval ti3 = new TimeInterval(c3, c1);
        TimeInterval ti4 = new TimeInterval(c4, c4);
        TimeInterval ti5 = new TimeInterval(c5, c4);

        AfterHourConstraint ahc = new AfterHourConstraint(18, 30);

        assertTrue(ahc.isSatisfiedBy(ti1));
        assertTrue(ahc.isSatisfiedBy(ti2));
        assertTrue(ahc.isSatisfiedBy(ti3));
        assertFalse(ahc.isSatisfiedBy(ti4));
        assertFalse(ahc.isSatisfiedBy(ti5));
    }

    @Test
    public void testBeforeHourConstraint() throws Exception {

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 25, 18, 35);
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 18, 0);
        Calendar c5 = Calendar.getInstance(); c5.set(2004, Calendar.JANUARY, 31, 15, 40);


        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c2, c4);
        TimeInterval ti3 = new TimeInterval(c3, c3);
        TimeInterval ti4 = new TimeInterval(c4, c4);
        TimeInterval ti5 = new TimeInterval(c5, c4);

        BeforeHourConstraint bhc = new BeforeHourConstraint(18, 30);

        assertFalse(bhc.isSatisfiedBy(ti1));
        assertTrue(bhc.isSatisfiedBy(ti2));
        assertFalse(bhc.isSatisfiedBy(ti3));
        assertTrue(bhc.isSatisfiedBy(ti4));
        assertTrue(bhc.isSatisfiedBy(ti5));
    }

    @Test
    public void testLaterThanConstraint() throws Exception {

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 25, 18, 35);
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 18, 0);
        Calendar c5 = Calendar.getInstance(); c5.set(2004, Calendar.JANUARY, 31, 15, 40);


        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c2, c4);
        TimeInterval ti3 = new TimeInterval(c3, c1);
        TimeInterval ti4 = new TimeInterval(c4, c4);
        TimeInterval ti5 = new TimeInterval(c5, c4);

        LaterThanConstraint ltc = new LaterThanConstraint(c2.getTimeInMillis()/1000);

        assertFalse(ltc.isSatisfiedBy(ti1));
        assertTrue(ltc.isSatisfiedBy(ti2));
        assertFalse(ltc.isSatisfiedBy(ti3));
        assertTrue(ltc.isSatisfiedBy(ti4));
        assertTrue(ltc.isSatisfiedBy(ti5));
    }

    @Test
    public void testEarlierThanConstraint() throws Exception {

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 25, 18, 35);
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 18, 0);
        Calendar c5 = Calendar.getInstance(); c5.set(2004, Calendar.JANUARY, 31, 15, 40);


        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c2, c4);
        TimeInterval ti3 = new TimeInterval(c3, c1);
        TimeInterval ti4 = new TimeInterval(c4, c4);
        TimeInterval ti5 = new TimeInterval(c5, c4);

        EarlierThanConstraint etc = new EarlierThanConstraint(c2.getTimeInMillis()/1000);

        assertTrue(etc.isSatisfiedBy(ti1));
        assertFalse(etc.isSatisfiedBy(ti2));
        assertTrue(etc.isSatisfiedBy(ti3));
        assertFalse(etc.isSatisfiedBy(ti4));
        assertFalse(etc.isSatisfiedBy(ti5));
    }

    @Test
    public void testGetCost() {

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30); //30/01/2004  18h30
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 25, 18, 35);
        Calendar c4 = Calendar.getInstance(); c4.set(2004, Calendar.JANUARY, 31, 18, 0);
        Calendar c5 = Calendar.getInstance(); c5.set(2004, Calendar.JANUARY, 31, 15, 40);

        TimeInterval ti1 = new TimeInterval(c1, c2);
        TimeInterval ti2 = new TimeInterval(c2, c4);
        TimeInterval ti3 = new TimeInterval(c3, c1);
        TimeInterval ti4 = new TimeInterval(c4, c4);
        TimeInterval ti5 = new TimeInterval(c5, c4);

        ArrayList<ScheduleConstraint> constraints = new ArrayList<>();
        constraints.add(new AfterHourConstraint(18, 30));
        constraints.add(new BeforeHourConstraint(18, 30));
        constraints.add(new LaterThanConstraint(c2.getTimeInMillis()/1000));
        constraints.add(new EarlierThanConstraint(c2.getTimeInMillis()/1000));

        ScheduleEvent se = new ScheduleEvent("", 0, new ArrayList<>(), ti1);
        se.initialize(new ArrayList<>(), new ArrayList<>());

        assertEquals(0, se.getCost(ti1));

        se.initialize(new ArrayList<>(), constraints);

        assertEquals(2, se.getCost(ti1)); // 0 1 1 0
        assertEquals(1, se.getCost(ti2)); // 0 1 0 0
        assertEquals(1, se.getCost(ti3)); // 0 1 0 0
        assertEquals(2, se.getCost(ti4)); // 1 0 0 1
        assertEquals(2, se.getCost(ti5)); // 1 0 0 1
    }
}
