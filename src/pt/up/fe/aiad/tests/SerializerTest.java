package pt.up.fe.aiad.tests;

import jade.core.AID;
import org.junit.Test;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.Serializer;
import pt.up.fe.aiad.scheduler.agentbehaviours.ABTBehaviour;
import pt.up.fe.aiad.scheduler.agentbehaviours.ADOPTBehaviour;
import pt.up.fe.aiad.utils.TimeInterval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SerializerTest {

    /*
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
    }*/


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

        TreeSet<AID> ags = new TreeSet<>();
        ags.add(agent1);ags.add(agent2);ags.add(agent3);ags.add(agent4);

        ScheduleEvent ev1 = new ScheduleEvent("cenas", 30*60, ags, new TimeInterval(c1, c2));
        String json = Serializer.EventProposalToJSON(ev1);
        ScheduleEvent ev2 = Serializer.EventProposalFromJSON(json);

        assertEquals(json,Serializer.EventProposalToJSON(ev2));
    }

    @Test
    public void testNoGoodJSON() {
        ABTBehaviour.NoGood ng = new ABTBehaviour.NoGood();

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 31, 18, 35);
        TimeInterval t1 = new TimeInterval(c1, c2);
        TimeInterval t2 = new TimeInterval(c2, c3);

        ng.v = t1;
        ng.cond = new HashMap<>();
        ng.cond.put("test1", t1);
        ng.cond.put("test2", t2);
        ng.tag = new TreeSet<>();
        ng.tag.add("test3");
        ng.tag.add("test4");
        ng.cost = 3;
        ng.exact = true;

        String json = Serializer.NoGoodToJSON(ng);
        ABTBehaviour.NoGood ng2 = Serializer.NoGoodFromJSON(json);

        assertEquals(ng2.v, ng.v);
        assertEquals(ng2.cond.get("test1"), ng.cond.get("test1"));
        assertEquals(ng2.cond.get("test2"), ng.cond.get("test2"));
        assertTrue(ng2.tag.containsAll(ng.tag) && ng.tag.containsAll(ng2.tag));
        assertEquals(ng2.cost, ng.cost);
        assertEquals(ng2.exact, ng.exact);
    }

    @Test
    public void testVariableJSON() {
        ABTBehaviour.Variable var = new ABTBehaviour.Variable();

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);

        var.v = new TimeInterval(c1, c2);
        var.agent = "test";

        String json = Serializer.VariableToJSON(var);
        ABTBehaviour.Variable var2 = Serializer.VariableFromJSON(json);

        assertEquals(var2.v, var.v);
        assertEquals(var2.agent, var.agent);
    }

    @Test
    public void testCostJSON() {
        ADOPTBehaviour.Cost cost = new ADOPTBehaviour.Cost();

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 31, 18, 35);
        TimeInterval t1 = new TimeInterval(c1, c2);
        TimeInterval t2 = new TimeInterval(c2, c3);

        cost.sender= "Agent1@192.168.23.52:1099/JADE-Dancing";
        cost.context = new HashMap<>();
        cost.context.put("Agent1@192.168.23.52:1099/JADE-Swimming", t1);
        cost.context.put("Agent2@192.168.23.52:1099/JADE-Dancing", t2);
        cost.lb = 3;
        cost.ub = 400;

        String json = Serializer.CostToJSON(cost);

        ADOPTBehaviour.Cost cost2 = Serializer.CostFromJSON(json);
        assertEquals(cost.sender, cost2.sender);
        assertEquals(cost.lb, cost2.lb);
        assertEquals(cost.ub, cost2.ub);
        assertEquals(t1, cost2.context.get("Agent1@192.168.23.52:1099/JADE-Swimming"));
        assertEquals(t2, cost2.context.get("Agent2@192.168.23.52:1099/JADE-Dancing"));
    }

    @Test
    public void testValueJSON() {
        ADOPTBehaviour.Value value = new ADOPTBehaviour.Value();
        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        TimeInterval t1 = new TimeInterval(c1, c2);
        value.sender = "Agent1@192.168.23.52:1099/JADE-Dancing";
        value.chosenValue = t1;

        String json = Serializer.ValueToJSON(value);

        ADOPTBehaviour.Value value2 = Serializer.ValueFromJSON(json);

        assertEquals(value.sender, value2.sender);
        assertEquals(value.chosenValue, value2.chosenValue);
    }

    @Test
    public void testThresholdJSON() {
        ADOPTBehaviour.Threshold t = new ADOPTBehaviour.Threshold();

        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 31, 18, 35);
        TimeInterval t1 = new TimeInterval(c1, c2);
        TimeInterval t2 = new TimeInterval(c2, c3);

        t.t= 2345;
        t.context = new HashMap<>();
        t.context.put("Agent1@192.168.23.52:1099/JADE-Swimming", t1);
        t.context.put("Agent2@192.168.23.52:1099/JADE-Dancing", t2);

        String json = Serializer.ThresholdToJSON(t);

        ADOPTBehaviour.Threshold thr2 = Serializer.ThresholdFromJSON(json);
        assertEquals(thr2.t, thr2.t);
        assertEquals(t1, t.context.get("Agent1@192.168.23.52:1099/JADE-Swimming"));
        assertEquals(t2, t.context.get("Agent2@192.168.23.52:1099/JADE-Dancing"));
    }

    @Test
    public void testContextJSON() {
        Calendar c1 = Calendar.getInstance(); c1.set(2004, Calendar.JANUARY, 30, 18, 30);
        Calendar c2 = Calendar.getInstance(); c2.set(2004, Calendar.JANUARY, 30, 18, 51);
        Calendar c3 = Calendar.getInstance(); c3.set(2004, Calendar.JANUARY, 31, 18, 35);
        TimeInterval t1 = new TimeInterval(c1, c2);
        TimeInterval t2 = new TimeInterval(c2, c3);

        HashMap<String, TimeInterval> context = new HashMap<>();
        context.put("Agent1@192.168.23.52:1099/JADE-Swimming", t1);
        context.put("Agent2@192.168.23.52:1099/JADE-Dancing", t2);

        String json = Serializer.ContextToJSON(context);

        HashMap<String, TimeInterval> context2 = Serializer.ContextFromJSON(json);

        assertEquals(t1, context2.get("Agent1@192.168.23.52:1099/JADE-Swimming"));
        assertEquals(t2, context2.get("Agent2@192.168.23.52:1099/JADE-Dancing"));
    }
}
