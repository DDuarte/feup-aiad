package pt.up.fe.aiad.scheduler;

public class ScheduleEvent {
    private String name;

    public ScheduleEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
