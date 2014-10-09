package pt.up.fe.aiad.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Random;

public class MainFrame extends Application {

    private Random rand = new Random();

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Generate grade for AIAD");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                String gradetxt = "Your grade is: " + rand.nextInt(21);
                final Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.WINDOW_MODAL);

                Button closeOnClick = new Button("Yay.");
                closeOnClick.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        dialogStage.close();
                    }
                });

                dialogStage.setScene(new Scene(VBoxBuilder.create().
                        children(new Text(gradetxt), closeOnClick).
                        alignment(Pos.CENTER).padding(new Insets(5)).build()));

                dialogStage.show();
            }

        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello AIAD!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
