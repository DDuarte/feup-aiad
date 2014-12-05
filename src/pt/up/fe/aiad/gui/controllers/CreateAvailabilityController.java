package pt.up.fe.aiad.gui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

public class CreateAvailabilityController {

    private Stage _stage;
    ObservableList<TimeInterval> _availabilities;

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

    @FXML
    private Button _doneButton;

    private long _eventDuration;

    private TimeInterval _maxBounds;

    public void initData(final Stage stage, ObservableList<TimeInterval> availabilities, long eventDuration, TimeInterval maxBounds) {
        _stage = stage;
        _availabilities = availabilities;
        _eventDuration = eventDuration;

        _minDate.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _minHours.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _minMinutes.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());

        _maxDate.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _maxHours.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());
        _maxMinutes.valueProperty().addListener((observable, oldValue, newValue) -> validateEventData());

        FXUtils.initializeHourChoiceBox(_minHours);
        FXUtils.initializeHourChoiceBox(_maxHours);
        FXUtils.initializeMinuteChoiceBox(_minMinutes);
        FXUtils.initializeMinuteChoiceBox(_maxMinutes);

        _maxBounds = maxBounds;

        _doneButton.setDisable(true);
    }

    public void validateEventData() {
        _doneButton.setDisable(true);

        Calendar minC = TimeInterval.calendarFromLocalDate(_minDate.getValue(), _minHours.getValue(), _minMinutes.getValue());
        Calendar maxC = TimeInterval.calendarFromLocalDate(_maxDate.getValue(), _maxHours.getValue(), _maxMinutes.getValue());
        if (minC == null || maxC == null)
            return;

        TimeInterval ti;
        try {
            ti = new TimeInterval(minC, maxC);
        } catch (IllegalArgumentException iae) {
            return;
        }

        if (_maxBounds.getStartDate() > ti.getStartDate() || _maxBounds.getEndDate() < ti.getEndDate())
            return;

        if (!ti.fits(_eventDuration))
            return;

        for (TimeInterval t : _availabilities) {
            if (t.overlaps(ti))
                return;
        }

        _doneButton.setDisable(false);
    }

    @FXML
    public void cancel() {
        _stage.close();
    }

    @FXML
    public void saveAvailability() {
        Calendar minC = TimeInterval.calendarFromLocalDate(_minDate.getValue(), _minHours.getValue(), _minMinutes.getValue());
        Calendar maxC = TimeInterval.calendarFromLocalDate(_maxDate.getValue(), _maxHours.getValue(), _maxMinutes.getValue());
        if (minC == null || maxC == null)
            return;

        TimeInterval ti = new TimeInterval(minC, maxC);

        _availabilities.add(ti);
        _stage.close();
    }
}
