package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.scheduler.constraints.AfterHourConstraint;
import pt.up.fe.aiad.scheduler.constraints.BeforeHourConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
}
