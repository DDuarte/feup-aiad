package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import org.json.JSONArray;
import org.json.JSONObject;
import pt.up.fe.aiad.scheduler.agentbehaviours.ABTBehaviour;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.*;

public class Serializer {

    public static ABTBehaviour.NoGood NoGoodFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        ABTBehaviour.NoGood ng = new ABTBehaviour.NoGood();

        ng.v = new TimeInterval(obj.getString("v"));
        ng.cond = new HashMap<>();

        JSONObject condObj = obj.getJSONObject("cond");
        Iterator<String> itr = condObj.keys();
        while (itr.hasNext()) {
            String name = itr.next();
            ng.cond.put(name, new TimeInterval(condObj.getString(name)));
        }

        JSONArray tags = obj.getJSONArray("tag");
        ng.tag = new TreeSet<>();
        for (int i = 0; i < tags.length(); ++i) {
            ng.tag.add(tags.getString(i));
        }

        ng.cost = obj.getInt("cost");
        ng.exact = obj.getBoolean("exact");

        return ng;
    }

    public static String NoGoodToJSON(ABTBehaviour.NoGood ng) {

        JSONObject condObj = new JSONObject();
        for (Map.Entry<String, TimeInterval> c : ng.cond.entrySet()) {
            condObj.put(c.getKey(), c.getValue().toString(true));
        }

        JSONObject obj = new JSONObject()
                .put("v", ng.v.toString(true))
                .put("cond", condObj)
                .put("tag", ng.tag)
                .put("cost", ng.cost)
                .put("exact", ng.exact);

        return obj.toString();
    }

    public static ABTBehaviour.Variable VariableFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        ABTBehaviour.Variable var = new ABTBehaviour.Variable();

        var.v = new TimeInterval(obj.getString("v"));
        var.agent = obj.getString("agent");

        return var;
    }

    public static String VariableToJSON(ABTBehaviour.Variable var) {
        JSONObject obj = new JSONObject()
                .put("v", var.v.toString(true))
                .put("agent", var.agent);

        return obj.toString();
    }

    public static String EventProposalToJSON(ScheduleEvent ev) {
        JSONObject obj = new JSONObject();
        obj.put("name", ev.getName());
        obj.put("duration", ev.getDuration());
        obj.put("maxbounds", ev.getMaxBounds().toString(true));

        ArrayList<String> partNames = new ArrayList<>();
        for (AID a : ev._participants) partNames.add(a.getName());

        obj.put("participants", partNames);

        return obj.toString();
    }

    public static ScheduleEvent EventProposalFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        JSONArray names = obj.getJSONArray("participants");

        TreeSet<AID> n = new TreeSet<>();
        for (int i=0; i < names.length(); i++) {
            n.add(new AID(names.getString(i), true));
        }

        return new ScheduleEvent(obj.getString("name"), obj.getLong("duration"), n, new TimeInterval(obj.getString("maxbounds")));
    }

    /*
                                    /* eventName - timeInterval *
    public static String EventsToJSON(Map<String, TimeInterval> events) {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, TimeInterval> entry : events.entrySet()) {
            obj.put(entry.getKey(), entry.getValue().toString(true));
        }

        return obj.toString();
    }

    public static Map<String, TimeInterval> EventsFromJSON(String json) {
        Map<String, TimeInterval> events = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Iterator<String> itr = obj.keys();

        while (itr.hasNext()) {
            String name = itr.next();
            events.put(name, new TimeInterval(obj.getString(name)));
        }

        return events;
    }

    public static String EventsAgentViewToJSON(Map<AID, Map<String, TimeInterval>> eventsAgentView) {
        JSONObject obj = new JSONObject();

        for (Map.Entry<AID, Map<String, TimeInterval>> entry : eventsAgentView.entrySet()) {
            obj.put(entry.getKey().getName(), EventsToJSON(entry.getValue()));
        }

        return obj.toString();
    }

    public static Map<AID, Map<String, TimeInterval>> EventsAgentViewFromJSON(String json) {
        Map<AID, Map<String, TimeInterval>> events = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Iterator<String> itr = obj.keys();

        while (itr.hasNext()) {
            String name = itr.next();
            events.put(new AID(name, true), EventsFromJSON(obj.getString(name)));
        }

        return events;
    }*/
}
