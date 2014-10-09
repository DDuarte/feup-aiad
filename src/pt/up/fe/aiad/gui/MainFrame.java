package pt.up.fe.aiad.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import jfxtras.scene.control.ListView;
import jfxtras.scene.layout.GridPane;

import pt.up.fe.aiad.scheduler.ScheduleEvent;

public class MainFrame extends Application {

    ObservableList<ScheduleEvent> eventList = FXCollections.observableArrayList();
    ListView<ScheduleEvent> eventListView;
    Label lLabel;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root);


        GridPane gPane = new GridPane();
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setPercentWidth(25);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(25);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);
        gPane.getColumnConstraints().addAll(cc0, cc1, cc2);


        gPane.add(getEventView(), new GridPane.C().col(0).row(0).colSpan(2));
        gPane.add(getNewEventButton(), new GridPane.C().col(0).row(1).colSpan(1));
        gPane.add(getRemoveEventButton(), new GridPane.C().col(1).row(1).colSpan(1));
        gPane.add(getInformationPanel(), new GridPane.C().col(2).row(0).colSpan(1).rowSpan(2));


        root.getChildren().addAll(gPane);

        primaryStage.setTitle("iSchedule");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createNewEvent() {
        //TODO
        eventList.add(new ScheduleEvent("Finish AIAD"));
    }

    private GridPane getInformationPanel() {
        GridPane lGridPane = new GridPane();
        lGridPane.setVgap(2.0);
        lGridPane.setHgap(2.0);

        // setup the grid so all the labels will not grow, but the rest will
        ColumnConstraints lColumnConstraintsAlwaysGrow = new ColumnConstraints();
        lColumnConstraintsAlwaysGrow.setHgrow(Priority.ALWAYS);
        ColumnConstraints lColumnConstraintsNeverGrow = new ColumnConstraints();
        lColumnConstraintsNeverGrow.setHgrow(Priority.NEVER);
        lGridPane.getColumnConstraints().addAll(lColumnConstraintsNeverGrow, lColumnConstraintsAlwaysGrow);

        lLabel = new Label("No event selected");
        lGridPane.add(lLabel, new GridPane.C().row(0).col(0).halignment(HPos.RIGHT));

        return lGridPane;
    }

    private Button getRemoveEventButton() {
        Button removeEventBtn = new Button("-");
        removeEventBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventList.remove(eventListView.getSelectedItem());
            }
        });
        removeEventBtn.setMaxWidth(Double.MAX_VALUE);
        return removeEventBtn;
    }

    private Button getNewEventButton() {
        Button addEventBtn = new Button("+");
        addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createNewEvent();
            }
        });
        addEventBtn.setMaxWidth(Double.MAX_VALUE);
        return addEventBtn;
    }

    private ListView<ScheduleEvent> getEventView() {
        eventListView = new ListView<ScheduleEvent>();
        eventListView.setItems(eventList);
        eventListView.selectedItemProperty().addListener(new ChangeListener<ScheduleEvent>() {
            @Override
            public void changed(ObservableValue<? extends ScheduleEvent> observable, ScheduleEvent oldValue, ScheduleEvent newValue) {
                lLabel.setText(newValue != null ? newValue.getName() : "No event selected");
            }
        });
        return eventListView;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
