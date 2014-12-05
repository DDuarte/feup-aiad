package pt.up.fe.aiad.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pt.up.fe.aiad.scheduler.constraints.ScheduleConstraint;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.ArrayList;

public class EditEventController {
    @FXML
    private ListView<TimeInterval> _domainView;
    private ObservableList<TimeInterval> _domain = FXCollections.observableArrayList();

    @FXML
    private ListView<ScheduleConstraint> _constraintView;
    private ObservableList<ScheduleConstraint> _constraints = FXCollections.observableArrayList();

    private boolean firstTimeSetup = false;

    public void initData(ArrayList<TimeInterval> evDomain, ArrayList<ScheduleConstraint> evConstraints) {
        _domainView.setItems(_domain);
        if (evDomain != null)
            _domain.addAll(evDomain);
        else
            firstTimeSetup = true;

        _constraintView.setItems(_constraints);
        if (evConstraints != null)
            _constraints.addAll(evConstraints);
    }
}
