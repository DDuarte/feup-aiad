package pt.up.fe.aiad.tests;

import jade.core.AID;
import org.junit.Test;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.utils.TimeInterval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void testEventsJSON() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar c1 = Calendar.getInstance(); c1.setTime(sdf.parse("10/10/2014"));
        Calendar c2 = Calendar.getInstance(); c2.setTime(sdf.parse("11/10/2014"));
        Calendar c3 = Calendar.getInstance(); c3.setTime(sdf.parse("20/10/2014"));

        Map<String, TimeInterval> events = new HashMap<>();
        events.put("fazer_aiad", new TimeInterval(c1, c2));
        events.put("entregar_aiad", new TimeInterval(c3, c3));

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

        Calendar c1 = Calendar.getInstance(); c1.setTime(sdf.parse("10/10/2014"));
        Calendar c2 = Calendar.getInstance(); c2.setTime(sdf.parse("11/10/2014"));
        Calendar c3 = Calendar.getInstance(); c3.setTime(sdf.parse("20/10/2014"));
        Calendar c4 = Calendar.getInstance(); c4.setTime(sdf.parse("12/10/2014"));

        Map<AID, Map<String, TimeInterval>> agentView = new HashMap<>();

        Map<String, TimeInterval> events1 = new HashMap<>();
        events1.put("a", new TimeInterval(c1, c2));
        events1.put("b", new TimeInterval(c3, c3));

        Map<String, TimeInterval> events2 = new HashMap<>();
        events2.put("a", new TimeInterval(c1, c4));
        events2.put("b", new TimeInterval(c3, c3));

        agentView.put(agent1, events1);
        agentView.put(agent2, events2);

        String eventsStr = Serializer.EventsAgentViewToJSON(agentView);

        Map<AID, Map<String, TimeInterval>> agentView2 = Serializer.EventsAgentViewFromJSON(eventsStr);

        assertEquals(2*agentView2.get(agent1).get("a").getDuration(),
                agentView2.get(agent2).get("a").getDuration());
    }


    @Test
    public void testEventProposalJSON() {
        AID agent1 = new AID("test1", true);
        AID agent2 = new AID("test2", true);
        AID agent3 = new AID("test3", true);
        AID agent4 = new AID("test4", true);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse("10/10/2014"));
            c2.setTime(sdf.parse("11/10/2014"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<AID> ags = new ArrayList<>();
        ags.add(agent1);ags.add(agent2);ags.add(agent3);ags.add(agent4);

        ScheduleEvent ev1 = new ScheduleEvent("cenas", 30*60, ags, new TimeInterval(c1, c2));
        String json = Serializer.EventProposalToJSON(ev1);
        ScheduleEvent ev2 = Serializer.EventProposalFromJSON(json);

        assertEquals(json,Serializer.EventProposalToJSON(ev2));
    }
}
