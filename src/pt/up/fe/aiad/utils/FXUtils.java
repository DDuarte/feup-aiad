package pt.up.fe.aiad.utils;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import org.controlsfx.dialog.Dialogs;

import java.time.LocalDate;

public class FXUtils {
    @SuppressWarnings("deprecation")
    static public void showExceptionDialog(Exception ex) {
        Dialogs.create()
                .owner(null)
                .title("Error | iScheduler")
                .masthead("Exception Encountered")
                .showException(ex);
    }

    public static void initializeHourChoiceBox(ChoiceBox<Integer> box) {
        for (int i = 0; i < 24; i++) {
            box.getItems().add(i);
        }

        box.setValue(0);
    }

    public static void initializeMinuteChoiceBox(ChoiceBox<Integer> box) {
        box.getItems().addAll(0, 30);
        box.setValue(0);
    }

    public static void initializeDateChoiceBox(DatePicker date) {
        date.setValue(LocalDate.now());
    }
}
