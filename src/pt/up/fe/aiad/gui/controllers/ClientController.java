package pt.up.fe.aiad.gui.controllers;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import pt.up.fe.aiad.scheduler.ScheduleEvent;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.FXUtils;

public class ClientController {
    private String _addressIp;
    private int _port;
    private String _nickname;
    private SchedulerAgent _agent;
    private AgentController _agentController;


    @FXML
    private Button _createNewEventButton;
    @FXML
    private ListView<ScheduleEvent> _eventsJoined;
    @FXML
    private ListView<ScheduleEvent> _eventsInvitedTo;
    @FXML
    private ListView<String> _allAgents;
    @FXML
    void initialize() {
        System.out.println("ClientController.init");
    }

    public void initData(String addressIp, int port, String nickname, SchedulerAgent.Type algorithm) {
        System.out.println("ClientController.initData");
        _addressIp = addressIp;
        _port = port;
        _nickname = nickname;
        _agent = new SchedulerAgent(algorithm);
        _allAgents.setItems(_agent.otherAgents);
        _eventsJoined.setItems(_agent._events);
        _eventsInvitedTo.setItems(_agent._invitedTo);
    }

    public void start() {
        System.out.println("ClientController.startServer: " + _nickname + " (" + _addressIp + ":" + _port + ")");
        if (MainController.container != null) {
            try {
                _agentController = MainController.container.acceptNewAgent(_nickname, _agent);
                _agentController.start();
            } catch (StaleProxyException e) {
                FXUtils.showExceptionDialog(e);
            }
        }
        else {
            ProfileImpl iae = new ProfileImpl(_addressIp, _port, _addressIp + ":" + Integer.toString(_port) + "/JADE", false);
            MainController.container = jade.core.Runtime.instance().createAgentContainer(iae);
            try {
                _agentController = MainController.container.acceptNewAgent(_nickname, _agent);
                _agentController.start();
            } catch (StaleProxyException e) {
                FXUtils.showExceptionDialog(e);
            }
        }
    }

    public void stop() {
        if (_agentController != null) {
            try {
                _agentController.kill();
            } catch (StaleProxyException e) {
                FXUtils.showExceptionDialog(e);
            }
        }
    }

    @FXML
    void createNewEventOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/createnewevent.fxml"));
            Stage stage = new Stage();
            stage.setTitle("New Event");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
            stage.setScene(scene);


            CreateEventController controller = loader.<CreateEventController>getController();
            controller.initData(_agent, stage);

            stage.show();
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }

    @FXML
    void acceptInvitation(ActionEvent event) {
        if (_eventsInvitedTo.getItems().size() > 0 && _eventsInvitedTo.getSelectionModel().getSelectedItem() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/editevent.fxml"));
                Stage stage = new Stage();
                stage.setTitle(_eventsInvitedTo.getSelectionModel().getSelectedItem().getName());
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);


                EditEventController controller = loader.<EditEventController>getController();
                controller.initData(stage, _eventsInvitedTo.getSelectionModel().getSelectedItem());

                stage.show();
            } catch (Exception e) {
                FXUtils.showExceptionDialog(e);
            }
        }
    }


    @SuppressWarnings("deprecation")
    @FXML
    void rejectInvitation(ActionEvent event) {

        if (_eventsInvitedTo.getItems().size() > 0 && _eventsInvitedTo.getSelectionModel().getSelectedItem() != null) {
            Action response = Dialogs.create()
                    .owner(null)
                    .title("Declining Invitation")
                    .message("Are you sure you want to leave this event?")
                    .showConfirm();

            if (response == Dialog.ACTION_YES) {
                _agent.rejectInvitation(_eventsInvitedTo.getSelectionModel().getSelectedItem());
            }
        }
    }
}
