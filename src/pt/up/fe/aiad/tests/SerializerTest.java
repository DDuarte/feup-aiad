package pt.up.fe.aiad.tests;

import jade.core.AID;
import org.junit.Test;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.utils.TimeInterval;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void testEventsJSON() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Map<String, TimeInterval> events = new HashMap<String, TimeInterval>();
        events.put("fazer_aiad", new TimeInterval(sdf.parse("10/10/2014"), sdf.parse("11/10/2014")));
        events.put("entregar_aiad", new TimeInterval(sdf.parse("20/10/2014"), sdf.parse("20/10/2014")));

        String eventsStr = Serializer.EventsToJSON(events);

        Map<String, TimeInterval> events2 = Serializer.EventsFromJSON(eventsStr);

        assertEquals(24*60*60, events2.get("fazer_aiad").getDuration());
        assertEquals(0, events2.get("entregar_aiad").getDuration());
    }

    @Test
    public void testEventsAgentViewJSON() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        AID agent1 = new AID("test1", true);
        AID agent2 = new AID("test2", true);

        Map<AID, Map<String, TimeInterval>> agentView = new HashMap<AID, Map<String, TimeInterval>>();

        Map<String, TimeInterval> events1 = new HashMap<String, TimeInterval>();
        events1.put("a", new TimeInterval(sdf.parse("10/10/2014"), sdf.parse("11/10/2014")));
        events1.put("b", new TimeInterval(sdf.parse("20/10/2014"), sdf.parse("20/10/2014")));

        Map<String, TimeInterval> events2 = new HashMap<String, TimeInterval>();
        events2.put("a", new TimeInterval(sdf.parse("10/10/2014"), sdf.parse("12/10/2014")));
        events2.put("b", new TimeInterval(sdf.parse("20/10/2014"), sdf.parse("20/10/2014")));

        agentView.put(agent1, events1);
        agentView.put(agent2, events2);

        String eventsStr = Serializer.EventsAgentViewToJSON(agentView);

        System.out.println(eventsStr);

        Map<AID, Map<String, TimeInterval>> agentView2 = Serializer.EventsAgentViewFromJSON(eventsStr);

        assertEquals(2*agentView2.get(agent1).get("a").getDuration(),
                agentView2.get(agent2).get("a").getDuration());
    }
}
