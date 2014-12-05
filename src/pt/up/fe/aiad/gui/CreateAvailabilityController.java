package pt.up.fe.aiad.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.utils.TimeInterval;

import java.time.LocalDate;
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

        for (int i=0; i < 24; i++) {
            _minHours.getItems().add(i);
            _maxHours.getItems().add(i);
        }
        _minHours.setValue(0);
        _maxHours.setValue(0);

        _minMinutes.getItems().addAll(0, 30);
        _minMinutes.setValue(0);
        _maxMinutes.getItems().addAll(0, 30);
        _maxMinutes.setValue(0);

        _maxBounds = maxBounds;

        _doneButton.setDisable(true);
    }

    public void validateEventData() {
        _doneButton.setDisable(true);

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

        TimeInterval ti = new TimeInterval(minC, maxC);

        _availabilities.add(ti);
        _stage.close();
    }
}
