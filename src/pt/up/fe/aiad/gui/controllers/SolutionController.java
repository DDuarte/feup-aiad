package pt.up.fe.aiad.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import pt.up.fe.aiad.scheduler.ScheduleEvent;

import java.util.ArrayList;

public class SolutionController {
    @FXML
    private TextArea _solutionView;

    public void initData(ArrayList<ScheduleEvent> evs) {
        String solutionText = "";

        if (evs.isEmpty())
            solutionText = "No events were allocated for your schedule. At least now you have some free time :)";
        else {
            for (ScheduleEvent ev : evs) {
                solutionText += ev.getName() + " (cost " + ev._currentCost + ") :\n";
                solutionText += ev._currentInterval.toString();
                solutionText += "\n\n";
            }
        }
        _solutionView.setText(solutionText);
    }

}
