package pt.up.fe.aiad.gui;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import pt.up.fe.aiad.utils.FXUtils;

import static jade.Boot.parseCmdLineArgs;

public class ServerController {
    private Stage _stage;
    private boolean _showGUI;
    private AgentContainer _container;

    @FXML
    void initialize() {
        System.out.println("ServerController.init");
    }

    public void initData(final Stage stage, boolean showGUI) {
        System.out.println("ServerController.initData");
        _stage = stage;
        _showGUI = showGUI;
    }

    public void start() {
        System.out.println("ServerController.startServer");

        _stage.setOnCloseRequest(event -> {
            if (_container != null) {
                try {
                    _container.kill();
                } catch (StaleProxyException e) {
                    FXUtils.showExceptionDialog(e);
                }
            }
        });

        ProfileImpl iae;

        if (_showGUI) {
            Properties pp = parseCmdLineArgs(new String[]{ "-gui" });
            iae = new ProfileImpl(pp);
        } else
            iae = new ProfileImpl(true);

        _container = Runtime.instance().createMainContainer(iae);
    }
}
