package pt.up.fe.aiad.gui;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
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
        _allAgents.setItems(_agent._otherAgents);
        _eventsJoined.setItems(_agent._events);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createnewevent.fxml"));
            Stage stage = new Stage();
            stage.setTitle("New Event");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
            stage.setScene(scene);


            CreateEventController controller = loader.<CreateEventController>getController();
            controller.initData();

            stage.show();

            _agent.addEvent(new ScheduleEvent("Cenas e tal", 30*60*2));
        } catch (Exception e) {
            FXUtils.showExceptionDialog(e);
        }
    }
}
