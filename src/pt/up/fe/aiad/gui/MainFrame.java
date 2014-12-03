package pt.up.fe.aiad.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.up.fe.aiad.utils.FXUtils;

public class MainFrame extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            primaryStage.setTitle("iScheduler");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
