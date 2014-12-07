package pt.up.fe.aiad.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pt.up.fe.aiad.scheduler.ScheduleEvent;

import java.util.ArrayList;

public class SolutionController {
    private Stage _stage;
    private ArrayList<ScheduleEvent> _evs;

    @FXML
    private TextArea _solutionView;

    public void initData(ArrayList<ScheduleEvent> evs) {
        _evs = evs;

        String solutionText = "";

        if (evs.size() == 0)
            solutionText = "No events were allocated for your schedule. At least now you have some free time :)";
        else {
            for (ScheduleEvent ev : evs) {
                solutionText += ev.getName() + ":\n";
                solutionText += ev._currentInterval.toString();
                solutionText += "\n\n";
            }
        }
        _solutionView.setText(solutionText);
    }

}
