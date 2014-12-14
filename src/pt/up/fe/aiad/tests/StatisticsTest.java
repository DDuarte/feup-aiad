package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.scheduler.Statistics;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void testSentMessage() throws Exception {
        Statistics s = new Statistics();

        s.sentMessage("X");
        s.sentMessage("X");
        s.sentMessage("X");
        s.sentMessage("Y");

        assertEquals(3, s.getSentMessages("X"));
        assertEquals(1, s.getSentMessages("Y"));
        assertEquals(0, s.getSentMessages("Z"));
    }

    @Test
    public void testReceivedMessage() throws Exception {
        Statistics s = new Statistics();

        s.receivedMessage("X");
        s.receivedMessage("X");
        s.receivedMessage("X");
        s.receivedMessage("Y");

        assertEquals(3, s.getReceivedMessages("X"));
        assertEquals(1, s.getReceivedMessages("Y"));
        assertEquals(0, s.getReceivedMessages("Z"));
    }
}
