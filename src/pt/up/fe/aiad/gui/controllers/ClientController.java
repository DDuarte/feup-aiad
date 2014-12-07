package pt.up.fe.aiad.gui.controllers;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.text.Text;
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
    @FXML
    SplitPane _controlView;
    @FXML
    Button _readyButton;
    @FXML
    Button _cancelButton;
    @FXML
    Text _waitingForOthersText;
    @FXML
    ProgressBar _algorithmProgress;
    @FXML
    Text _progressBarText;

    public void initData(String addressIp, int port, String nickname, SchedulerAgent.Type algorithm) {
        System.out.println("ClientController.initData");
        _addressIp = addressIp;
        _port = port;
        _nickname = nickname;
        _agent = new SchedulerAgent(algorithm);
        _allAgents.setItems(_agent.otherAgents);
        _eventsJoined.setItems(_agent._events);
        _eventsInvitedTo.setItems(_agent._invitedTo);
        _cancelButton.setDisable(true);
        _waitingForOthersText.setVisible(false);
        _algorithmProgress.setVisible(false);
        _progressBarText.setVisible(false);
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

        _agent.isReady.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                _controlView.setDisable(true);
                _readyButton.setDisable(true);
                _waitingForOthersText.setVisible(true);
                _cancelButton.setDisable(false);
            }
            else {
                _controlView.setDisable(false);
                _readyButton.setDisable(false);
                _cancelButton.setDisable(true);
                _waitingForOthersText.setVisible(false);
            }
        });

        _agent.allReady.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                _controlView.setDisable(true);
                _readyButton.setDisable(true);
                _waitingForOthersText.setVisible(false);
                _cancelButton.setDisable(true);
                _algorithmProgress.setVisible(true);
                _progressBarText.setVisible(true);
                _agent.initializeAlgorithm();
            }
        });

        _agent.algorithmFinished.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                
            }
        });
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
                controller.initData(stage, _eventsInvitedTo.getSelectionModel().getSelectedItem(), _agent);

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

    @SuppressWarnings("deprecation")
    @FXML
    void leaveEvent(ActionEvent event) {

        if (_eventsJoined.getItems().size() > 0 && _eventsJoined.getSelectionModel().getSelectedItem() != null) {
            Action response = Dialogs.create()
                    .owner(null)
                    .title("Declining Invitation")
                    .message("Are you sure you want to leave this event?")
                    .showConfirm();

            if (response == Dialog.ACTION_YES) {
                _agent.leaveEvent(_eventsJoined.getSelectionModel().getSelectedItem());
            }
        }
    }

    @FXML
    void editEvent(ActionEvent event) {
        if (_eventsJoined.getItems().size() > 0 && _eventsJoined.getSelectionModel().getSelectedItem() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/editevent.fxml"));
                Stage stage = new Stage();
                stage.setTitle(_eventsJoined.getSelectionModel().getSelectedItem().getName());
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("../views/main.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);


                EditEventController controller = loader.<EditEventController>getController();
                controller.initData(stage, _eventsJoined.getSelectionModel().getSelectedItem(), _agent);

                stage.show();
            } catch (Exception e) {
                FXUtils.showExceptionDialog(e);
            }
        }
    }

    @FXML
    void inviteToEvent(ActionEvent event) {
        if (_eventsJoined.getItems().size() > 0 && _eventsJoined.getSelectionModel().getSelectedItem() != null) {
            if (_allAgents.getItems().size() > 0 && _allAgents.getSelectionModel().getSelectedItem() != null) {
                if (_eventsJoined.getSelectionModel().getSelectedItem()._participants.contains(_agent.agentNameToAid.get(_allAgents.getSelectionModel().getSelectedItem()))) {
                    Dialogs.create()
                            .owner(null)
                            .title("Invitation Failed")
                            .message("That user is already participating in the event")
                            .showWarning();
                }
                else
                    _agent.addAgentToEvent(_allAgents.getSelectionModel().getSelectedItem(), _eventsJoined.getSelectionModel().getSelectedItem());
            }
        }
    }

    @FXML
    void agentReady(ActionEvent event) {
        if (_eventsInvitedTo.getItems().size() > 0) {
            Dialogs.create()
                    .owner(null)
                    .title("Not Ready Yet")
                    .message("Please reply to your pending invitations first")
                    .showWarning();
            return;
        }
        _agent.setReady();
    }

    @FXML
    void cancelReady(ActionEvent event) {
        _agent.cancelReady();
    }
}
