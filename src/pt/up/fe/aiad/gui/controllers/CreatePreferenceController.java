package pt.up.fe.aiad.gui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pt.up.fe.aiad.scheduler.constraints.*;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;

public class CreatePreferenceController {

    private Stage _stage;
    ObservableList<ScheduleConstraint> _constraints;

    @FXML Tab _timeTab;
    @FXML Tab _hourTab;
    @FXML Button _saveButton;
    @FXML Button _cancelButton;

    @FXML ChoiceBox<Integer> _timeMinutesChoiceBox;
    @FXML ChoiceBox<Integer> _timeHoursChoiceBox;
    @FXML DatePicker _timeDatePicker;
    @FXML RadioButton _timeBeforeRadioButton;
    @FXML RadioButton _timeAfterRadioButton;

    @FXML ChoiceBox<Integer> _hourMinutesChoiceBox;
    @FXML ChoiceBox<Integer> _hourHoursChoiceBox;
    @FXML RadioButton _hourBeforeRadioButton;
    @FXML RadioButton _hourAfterRadioButton;

    public void initData(final Stage stage, ObservableList<ScheduleConstraint> constraints) {
        _stage = stage;
        _constraints = constraints;

        _timeHoursChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _timeMinutesChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _timeDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _timeBeforeRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _timeAfterRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());

        _hourHoursChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _hourMinutesChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _hourBeforeRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());
        _hourAfterRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> validateConstraintData());

        ToggleGroup tg = new ToggleGroup();
        _timeAfterRadioButton.setToggleGroup(tg);
        _timeBeforeRadioButton.setToggleGroup(tg);
        _hourAfterRadioButton.setToggleGroup(tg);
        _hourBeforeRadioButton.setToggleGroup(tg);

        FXUtils.initializeHourChoiceBox(_timeHoursChoiceBox);
        FXUtils.initializeHourChoiceBox(_hourHoursChoiceBox);
        FXUtils.initializeMinuteChoiceBox(_timeMinutesChoiceBox);
        FXUtils.initializeMinuteChoiceBox(_hourMinutesChoiceBox);

        _saveButton.setDisable(true);
    }

    private void validateConstraintData() {
        _saveButton.setDisable(true);

        if (_timeTab.isSelected()) {
            if (_timeDatePicker.getValue() == null)
                return;

            if (!_timeAfterRadioButton.isSelected() && !_timeBeforeRadioButton.isSelected())
                return;
        } else {
            if (!_hourAfterRadioButton.isSelected() && !_hourBeforeRadioButton.isSelected())
                return;
        }

        _saveButton.setDisable(false);
    }

    @FXML
    public void cancel() {
        _stage.close();
    }

    @FXML
    public void savePreference() {
        ScheduleConstraint sc;

        if (_timeTab.isSelected()) {
            Calendar c = TimeInterval.calendarFromLocalDate(_timeDatePicker.getValue(), _timeHoursChoiceBox.getValue(),
                    _timeMinutesChoiceBox.getValue());
            if (c == null) {
                return;
            }

            long time = c.getTimeInMillis() / 1000;

            if (_timeAfterRadioButton.isSelected()) {
                sc = new LaterThanConstraint(time);
            } else {
                sc = new EarlierThanConstraint(time);
            }
        } else {
            int hours = _hourHoursChoiceBox.getValue();
            int minutes = _hourMinutesChoiceBox.getValue();
            if (_hourAfterRadioButton.isSelected()) {
                sc = new AfterHourConstraint(hours, minutes);
            } else {
                sc = new BeforeHourConstraint(hours, minutes);
            }
        }

        _constraints.add(sc);
        _stage.close();
    }
}
