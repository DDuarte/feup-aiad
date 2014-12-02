package pt.up.fe.aiad.gui;

import javafx.fxml.FXML;

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
    }
}
