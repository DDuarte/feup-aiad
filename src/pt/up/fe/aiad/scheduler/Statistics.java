package pt.up.fe.aiad.scheduler;

import java.util.HashMap;

public class Statistics {
    private HashMap<String, Integer> _sentMessages = new HashMap<>();
    private HashMap<String, Integer> _receivedMessages = new HashMap<>();
    private String _variableName;

    public void setVariableName(String name) {
        _variableName = name;
    }

    public String getVariableName() {
        return _variableName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(_variableName);
        sb.append("] sent: {");

        // for (Map.Entry<>)

        sb.append("} received: {");

        sb.append("}");
        return sb.toString();
    }

    public int getSentMessages(String messageType) {
        return _sentMessages.getOrDefault(messageType, 0);
    }

    public int getReceivedMessages(String messageType) {
        return _receivedMessages.getOrDefault(messageType, 0);
    }

    public void sentMessage(String messageType) {
        _sentMessages.put(messageType, _sentMessages.getOrDefault(messageType, 0) + 1);
    }

    public void receivedMessage(String messageType) {
        _receivedMessages.put(messageType, _receivedMessages.getOrDefault(messageType, 0) + 1);
    }
}
