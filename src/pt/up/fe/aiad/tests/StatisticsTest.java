package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.scheduler.Statistics;
import static org.junit.Assert.*;

public class StatisticsTest {

    @Test
    public void testSentMessage() throws Exception {
        Statistics s = new Statistics();

        s.sentMessage("A", "X");
        s.sentMessage("A", "X");
        s.sentMessage("B", "X");
        s.sentMessage("B", "Y");

        assertEquals(2, s.getSentMessages("A", "X"));
        assertEquals(0, s.getSentMessages("A", "Y"));
        assertEquals(1, s.getSentMessages("B", "X"));
        assertEquals(1, s.getSentMessages("B", "Y"));
        assertEquals(0, s.getSentMessages("C", "X"));
    }

    @Test
    public void testReceivedMessage() throws Exception {
        Statistics s = new Statistics();

        s.receivedMessage("A", "X");
        s.receivedMessage("A", "X");
        s.receivedMessage("B", "X");
        s.receivedMessage("B", "Y");

        assertEquals(2, s.getReceivedMessages("A", "X"));
        assertEquals(0, s.getReceivedMessages("A", "Y"));
        assertEquals(1, s.getReceivedMessages("B", "X"));
        assertEquals(1, s.getReceivedMessages("B", "Y"));
        assertEquals(0, s.getReceivedMessages("C", "X"));
    }
}
