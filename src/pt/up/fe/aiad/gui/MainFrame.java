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
            Parent root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
            primaryStage.setTitle("iScheduler");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("views/main.css").toExternalForm());

            primaryStage.setScene(scene);
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
