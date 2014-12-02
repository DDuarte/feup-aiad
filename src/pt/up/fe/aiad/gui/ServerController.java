package pt.up.fe.aiad.gui;

import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import javafx.fxml.FXML;

import static jade.Boot.parseCmdLineArgs;

public class ServerController {
    private boolean _showGUI;

    @FXML
    void initialize() {
        System.out.println("ServerController.init");
    }

    public void initData(boolean showGUI) {
        System.out.println("ServerController.initData");
        _showGUI = showGUI;
    }

    public void startServer() {
        System.out.println("ServerController.startServer");
        String ar[] = {"-gui"};
        ProfileImpl iae = null;
        Properties pp = parseCmdLineArgs(ar);

        if (_showGUI)
            iae = new ProfileImpl(pp);
        else
            iae = new ProfileImpl(true);

        Runtime.instance().setCloseVM(true);
        Runtime.instance().createMainContainer(iae);
    }
}
