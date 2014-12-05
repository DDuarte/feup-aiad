package pt.up.fe.aiad.gui.controllers;

import jade.core.AID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.constraints.ScheduleConstraint;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.TimeInterval;

import java.util.Calendar;
import java.util.stream.Collectors;

public class EditEventController {
    @FXML
    private ListView<TimeInterval> _domainView;
    private ObservableList<TimeInterval> _domain = FXCollections.observableArrayList();

    @FXML
    private ListView<ScheduleConstraint> _constraintView;
    private ObservableList<ScheduleConstraint> _constraints = FXCollections.observableArrayList();

    @FXML
    private ListView<String> _participantsView;
    private ObservableList<String> _participants = FXCollections.observableArrayList();

    @FXML
    private Button _doneButton;

    @FXML
    private TextField _durationTextField;
    @FXML
    private TextField _minDateTextField;
    @FXML
    private TextField _maxDateTextField;

    private boolean firstTimeSetup = false;

    private Stage _stage;

    private long _duration;
    private TimeInterval _maxBounds;

    public void initData(final Stage stage, ScheduleEvent ev) {
        _domainView.setItems(_domain.sorted());
        _constraintView.setItems(_constraints);
        _participantsView.setItems(_participants.sorted());
        _stage = stage;

        if (ev.isInitialized()) {
            _domain.addAll(ev._availableIntervals);
            _constraints.addAll(ev._constraints);
        } else
            firstTimeSetup = true;

        _participants.addAll(ev._participants.stream().map(AID::getName).collect(Collectors.toList()));

        _duration = ev.getDuration();

        _maxBounds = ev.getMaxBounds();

        long minutes = _duration / 60;
        _durationTextField.setText(Long.toString(minutes / 60) + "h" + Long.toString(minutes % 60) + "m");

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(_maxBounds.getStartDate() * 1000);
        _minDateTextField.setText(c1.getTime().toString());
        c1.setTimeInMillis(_maxBounds.getEndDate() * 1000);
        _maxDateTextField.setText(c1.getTime().toString());
    }

    void validateData() {
        _doneButton.setDisable(true);

        _doneButton.setDisable(false);
    }

    @FXML
    void addAvailability(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/createavailability.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Availability");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            CreateAvailabilityController controller = loader.<CreateAvailabilityController>getController();
            controller.initData(stage, _domain, _duration, _maxBounds);

            stage.show();
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }

        validateData();
    }

    @FXML
    void removeAvailability(ActionEvent event) {
        if (_domainView.getItems().size() > 0 && _domainView.getSelectionModel().getSelectedItem() != null) {
            _domain.remove(_domainView.getSelectionModel().getSelectedItem());
            validateData();
        }
    }

    @FXML
    void addPreference(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/createpreference.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Preference");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            CreatePreferenceController controller = loader.<CreatePreferenceController>getController();
            controller.initData(stage, _constraints);

            stage.show();
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }

        validateData();
    }

    @FXML
    void removePreference(ActionEvent event) {
        if (_constraintView.getItems().size() > 0 && _constraintView.getSelectionModel().getSelectedItem() != null) {
            _constraints.remove(_constraintView.getSelectionModel().getSelectedItem());
            validateData();
        }
    }

    @FXML
    void cancelEdit() {
        _stage.close();
    }
}
