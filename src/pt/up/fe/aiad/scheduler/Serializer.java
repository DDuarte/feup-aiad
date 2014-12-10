package pt.up.fe.aiad.scheduler;

import jade.core.AID;
import org.json.JSONArray;
import org.json.JSONObject;
import pt.up.fe.aiad.scheduler.agentbehaviours.ABTBehaviour;
import pt.up.fe.aiad.scheduler.agentbehaviours.ADOPTBehaviour;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.*;

public class Serializer {

    public static String ContextToJSON(HashMap<String, TimeInterval> context) {
        JSONObject condObj = new JSONObject();
        for (Map.Entry<String, TimeInterval> e : context.entrySet()) {
            condObj.put(e.getKey(), e.getValue().toString(true));
        }
        return condObj.toString();
    }

    public static HashMap<String, TimeInterval> ContextFromJSON(String json) {
        HashMap<String, TimeInterval> context = new HashMap<>();
        JSONObject obj = new JSONObject(json);

        Iterator<String> itr = obj.keys();
        while (itr.hasNext()) {
            String name = itr.next();
            context.put(name, new TimeInterval(obj.getString(name)));
        }

        return context;
    }

    public static String ThresholdToJSON(ADOPTBehaviour.Threshold t) {
        JSONObject condObj = new JSONObject();
        for (Map.Entry<String, TimeInterval> e : t.context.entrySet()) {
            condObj.put(e.getKey(), e.getValue().toString(true));
        }

        JSONObject obj = new JSONObject()
                .put("t", t.t)
                .put("context", condObj);

        return obj.toString();
    }

    public static ADOPTBehaviour.Threshold ThresholdFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        ADOPTBehaviour.Threshold t = new ADOPTBehaviour.Threshold();

        t.t = obj.getInt("t");
        t.context = new HashMap<>();
        JSONObject contextObj = obj.getJSONObject("context");
        Iterator<String> itr = contextObj.keys();
        while (itr.hasNext()) {
            String name = itr.next();
            t.context.put(name, new TimeInterval(contextObj.getString(name)));
        }

        return t;
    }

    public static String ValueToJSON(ADOPTBehaviour.Value v) {
        JSONObject obj = new JSONObject()
                .put("sender", v.sender)
                .put("value", v.chosenValue.toString(true));

        return obj.toString();
    }

    public static ADOPTBehaviour.Value ValueFromJSON(String json) {
        JSONObject obj = new JSONObject(json);
        ADOPTBehaviour.Value v = new ADOPTBehaviour.Value();
        v.sender = obj.getString("sender");
        v.chosenValue = new TimeInterval(obj.getString("value"));

        return v;
    }

    public static String CostToJSON(ADOPTBehaviour.Cost c) {
        JSONObject condObj = new JSONObject();
        for (Map.Entry<String, TimeInterval> e : c.context.entrySet()) {
            condObj.put(e.getKey(), e.getValue().toString(true));
        }

        JSONObject obj = new JSONObject()
                .put("sender", c.sender)
                .put("context", condObj)
                .put("lb", c.lb)
                .put("ub", c.ub);

        return obj.toString();
    }

    public static ADOPTBehaviour.Cost CostFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        ADOPTBehaviour.Cost cost = new ADOPTBehaviour.Cost();

        cost.sender = obj.getString("sender");
        cost.context = new HashMap<>();
        JSONObject contextObj = obj.getJSONObject("context");
        Iterator<String> itr = contextObj.keys();
        while (itr.hasNext()) {
            String name = itr.next();
            cost.context.put(name, new TimeInterval(contextObj.getString(name)));
        }
        cost.lb = obj.getInt("lb");
        cost.ub = obj.getInt("ub");

        return cost;
    }

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
}
