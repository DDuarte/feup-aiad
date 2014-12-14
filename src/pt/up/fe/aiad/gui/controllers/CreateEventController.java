package pt.up.fe.aiad.gui.controllers;


import jade.core.AID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.StringUtils;
import pt.up.fe.aiad.utils.TimeInterval;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.TreeSet;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class CreateEventController {

    @FXML
    private TextField _eventName;
    @FXML
    private TextField _hours;
    @FXML
    private ChoiceBox<Integer> _minutes;
    @FXML
    private Button _confirmButton;
    @FXML
    private ListView<String> _otherAgents;

    private SchedulerAgent _agent;
    private Stage _stage;

    @FXML
    private DatePicker _minDate;
    @FXML
    private ChoiceBox<Integer> _minHours;
    @FXML
    private ChoiceBox<Integer> _minMinutes;

    @FXML
    private DatePicker _maxDate;
    @FXML
    private ChoiceBox<Integer> _maxHours;
    @FXML
    private ChoiceBox<Integer> _maxMinutes;


    public void initData(SchedulerAgent agent, final Stage stage) {
        System.out.println("ClientController.initData");

        _minutes.getItems().addAll(0, 30);
        _minutes.setValue(0);
        _agent = agent;
        _stage = stage;

        _confirmButton.setDisable(true);


        _eventName.textProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _hours.textProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _minutes.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());

        _minDate.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _minHours.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _minMinutes.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());

        _maxDate.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _maxHours.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _maxMinutes.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());

        _otherAgents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _otherAgents.setItems(agent.otherAgents);

        FXUtils.initializeHourChoiceBox(_minHours);
        FXUtils.initializeHourChoiceBox(_maxHours);
        FXUtils.initializeMinuteChoiceBox(_minMinutes);
        FXUtils.initializeMinuteChoiceBox(_maxMinutes);
        FXUtils.initializeDateChoiceBox(_minDate);
        FXUtils.initializeDateChoiceBox(_maxDate);
    }

    @SuppressWarnings("MagicConstant")
    private void validateEventData() {
        _confirmButton.setDisable(true);

        if (StringUtils.isNullOrEmpty(_eventName.getText()))
            return;

        if (_eventName.getText().contains("-")) {
            Dialogs.create()
                    .title("Validation Error")
                    .message("Event name cannot have character '-")
                    .showError();
            return;
        }

        if (StringUtils.isNullOrEmpty(_hours.getText()))
            return;
        if (_eventName.getText().length() > 64)
            return;
        int val;
        try {
            val = Integer.parseInt(_hours.getText());
        } catch (NumberFormatException nfe) {
            return;
        }

        if (val < 0 || val + _minutes.getValue() <= 0)
            return;

        LocalDate minLd = _minDate.getValue();
        if (minLd == null)
            return;
        Calendar minC =  Calendar.getInstance();
        minC.set(minLd.getYear(), minLd.getMonthValue()-1, minLd.getDayOfMonth(), _minHours.getValue(), _minMinutes.getValue(), 0);

        LocalDate maxLd = _maxDate.getValue();
        if (maxLd == null)
            return;
        Calendar maxC =  Calendar.getInstance();
        maxC.set(maxLd.getYear(), maxLd.getMonthValue()-1, maxLd.getDayOfMonth(), _maxHours.getValue(), _maxMinutes.getValue(), 0);

        TimeInterval ti;
        try {
            ti = new TimeInterval(minC, maxC);
        } catch (IllegalArgumentException iae) {
            return;
        }

        if (!ti.fits(Integer.parseInt(_hours.getText()) * 3600 + _minutes.getValue() * 60))
            return;

        _confirmButton.setDisable(false);
    }

    @FXML
    public void saveEvent(ActionEvent event) {
        TreeSet<AID> agents = new TreeSet<>();
        agents.add (_agent.getAID());

        agents.addAll(_otherAgents.getSelectionModel().getSelectedItems().stream().map(_agent.agentNameToAid::get).collect(Collectors.toList()));

        Calendar minC = TimeInterval.calendarFromLocalDate(_minDate.getValue(), _minHours.getValue(), _minMinutes.getValue());
        Calendar maxC = TimeInterval.calendarFromLocalDate(_maxDate.getValue(), _maxHours.getValue(), _maxMinutes.getValue());
        if (minC == null || maxC == null)
            return;

        TimeInterval ti = new TimeInterval(minC, maxC);

        ScheduleEvent _newEvent = new ScheduleEvent(_eventName.getText(), Integer.parseInt(_hours.getText()) * 3600 + _minutes.getValue() * 60, agents, ti);
        _agent.dispatchInvitations(_newEvent);
        _stage.close();
    }

    @FXML
    public void cancelCreation(ActionEvent event) {
        _stage.close();
    }
}
