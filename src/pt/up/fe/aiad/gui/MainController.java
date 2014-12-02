package pt.up.fe.aiad.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pt.up.fe.aiad.utils.FXUtils;
import pt.up.fe.aiad.utils.IPAddressValidator;
import pt.up.fe.aiad.utils.StringUtils;

import java.util.Random;

public class MainController {

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

    private static String _exampleNicknames[] = new String[] { "John", "Ann", "Tom", "Alicia", "Edward", "Sarah" };

    @FXML
    void initialize() {
        _nicknameTextField.textProperty().addListener((observable, oldValue, newValue) -> validateClientInput());
        _addressTextField.textProperty().addListener((observable, oldValue, newValue) -> validateClientInput());

        _addressTextField.setText("127.0.0.1:1199");
        _nicknameTextField.setText(getNewNickname());
    }

    private static String getNewNickname() {
        return _exampleNicknames[new Random().nextInt(_exampleNicknames.length)];
    }

    @FXML
    void startClientButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            Stage stage = new Stage();
            stage.setTitle(_nicknameTextField.getText() + " | iScheduler");
            stage.setScene(new Scene(loader.load()));

            String addressSplit[] = _addressTextField.getText().split(":");

            ClientController controller = loader.<ClientController>getController();
            controller.initData(addressSplit[0], Integer.parseInt(addressSplit[1]), _nicknameTextField.getText());
            controller.start();

            stage.show();

            _nicknameTextField.setText(getNewNickname());
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }

    @FXML
    void startServerButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Server | iScheduler");
            stage.setScene(new Scene(loader.load()));

            ServerController controller = loader.<ServerController>getController();
            controller.initData(stage, _showGUICheckBox.isSelected());
            controller.start();

            stage.show();

            _startServerButton.setDisable(true);
            stage.setOnHiding(eventHiding -> _startServerButton.setDisable(false));
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

        if (!IPAddressValidator.validate(_addressTextField.getText(), true))
            return;

        _startClientButton.setDisable(false);
    }
}
