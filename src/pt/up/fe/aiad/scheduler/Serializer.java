package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import org.json.JSONArray;
import org.json.JSONObject;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class Serializer {

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

        ArrayList<AID> n = new ArrayList<>();
        for (int i=0; i < names.length(); i++) {
            n.add(new AID(names.getString(i), true));
        }

        return new ScheduleEvent(obj.getString("name"), obj.getLong("duration"), n, new TimeInterval(obj.getString("maxbounds")));
    }
                                    /* eventName - timeInterval */
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
    }
}
