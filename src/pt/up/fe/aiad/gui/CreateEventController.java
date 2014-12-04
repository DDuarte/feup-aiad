package pt.up.fe.aiad.gui;


import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class CreateEventController {
    @FXML
    private ChoiceBox<Integer> _minutes;

    public void initData() {
        System.out.println("ClientController.initData");

        _minutes.getItems().addAll(0, 30);
        _minutes.setValue(0);
    }
}
