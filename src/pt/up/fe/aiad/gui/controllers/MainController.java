package pt.up.fe.aiad.gui.controllers;

import jade.wrapper.AgentContainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.IPAddressValidator;
import pt.up.fe.aiad.utils.StringUtils;

import java.util.Random;

@SuppressWarnings("deprecation")
public class MainController {

    private static Random r = new Random();

    public static AgentContainer container = null;

    @FXML
    private TextField _addressTextField;
    @FXML
    private TextField _nicknameTextField;
    @FXML
    private CheckBox _showGUICheckBox;
    @FXML
    private Button _startClientButton;
    @FXML
    private Button _startServerButton;
    @FXML
    private ChoiceBox<SchedulerAgent.Type> _algorithmChoiceBox;

    private static String _exampleNicknames[] = new String[] {
            "Luis", "Duarte", "Miguel", "Ruben", "Adolf", "Anne", "Frank", "Katie", "Natalie", "Benji", "Makoto Yooko", "Dovahkiin", "Judith",
            "Zelda", "Link", "Red", "Ash", "Mario", "Luigi", "Wario", "Lucina", "Kirby", "Pikachu", "Diglet", "Misty", "Brock", "Ezio", "Captain Falcon",
            "Captain Shepard", "Master Chief", "Spyro", "Ryu", "Megaman", "Lara Croft", "Gordon Freeman", "Solid Snake", "Samus", "Sonic", "Tails",
            "Kratos", "Donkey Kong", "Dante", "Obi Wan Kenobi", "Anakin Skywalker", "Luke Skywalker", "Bomberman"
    };

    @FXML
    void initialize() {
        _nicknameTextField.textProperty().addListener((observable, oldValue, newValue) -> validateClientInput());
        _addressTextField.textProperty().addListener((observable, oldValue, newValue) -> validateClientInput());

        _addressTextField.setText("127.0.0.1:1099");
        _nicknameTextField.setText(getNewNickname());
        _algorithmChoiceBox.getItems().addAll(SchedulerAgent.Type.values());
        _algorithmChoiceBox.setValue(SchedulerAgent.Type.values()[0]);
    }

    private static String getNewNickname() {
        return _exampleNicknames[r.nextInt(_exampleNicknames.length)];
    }

    @FXML
    void startClientButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/client.fxml"));
            Stage stage = new Stage();
            stage.setTitle(_nicknameTextField.getText() + " | " + _algorithmChoiceBox.getValue() + " | iScheduler");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
            stage.setScene(scene);

            String addressSplit[] = _addressTextField.getText().split(":");

            ClientController controller = loader.<ClientController>getController();
            controller.initData(addressSplit[0], Integer.parseInt(addressSplit[1]),
                    _nicknameTextField.getText(), _algorithmChoiceBox.getValue(), stage);
            controller.start();

            stage.show();

            stage.setOnHiding(eventHiding -> controller.stop());

            _nicknameTextField.setText(getNewNickname());
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }

    @FXML
    void startServerButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/server.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Server | iScheduler");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
            stage.setScene(scene);

            ServerController controller = loader.<ServerController>getController();
            controller.initData(stage, _showGUICheckBox.isSelected());
            controller.start();

            stage.show();

            _startServerButton.setDisable(true);
            _addressTextField.setDisable(true);
            stage.setOnHiding(eventHiding -> {
                _startServerButton.setDisable(false);
                _addressTextField.setDisable(false);
            });
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }

    private void validateClientInput() {
        _startClientButton.setDisable(true);

        if (StringUtils.isNullOrEmpty(_addressTextField.getText()))
            return;

        if (StringUtils.isNullOrEmpty(_nicknameTextField.getText()))
            return;

        if (_nicknameTextField.getText().contains("-")) {
            Dialogs.create()
                    .title("Validation Error")
                    .message("Event name cannot have character '-")
                    .showError();
            return;
        }

        if (!IPAddressValidator.validate(_addressTextField.getText(), true))
            return;

        _startClientButton.setDisable(false);
    }
}
