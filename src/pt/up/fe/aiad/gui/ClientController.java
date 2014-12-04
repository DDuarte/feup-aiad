package pt.up.fe.aiad.gui;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pt.up.fe.aiad.scheduler.SchedulerAgent;
import pt.up.fe.aiad.utils.FXUtils;

public class ClientController {
    private String _addressIp;
    private int _port;
    private String _nickname;
    private SchedulerAgent _agent;
    private AgentController _agentController;

    @FXML
    private ListView<String> _allAgents;


    @FXML
    void initialize() {
        System.out.println("ClientController.init");
    }

    public void initData(String addressIp, int port, String nickname) {
        System.out.println("ClientController.initData");
        _addressIp = addressIp;
        _port = port;
        _nickname = nickname;
        _agent = new SchedulerAgent(SchedulerAgent.Type.ABT); //TODO add type
        _allAgents.setItems(_agent._otherAgents);
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
}
