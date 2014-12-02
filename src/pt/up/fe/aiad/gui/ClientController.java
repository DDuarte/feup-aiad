package pt.up.fe.aiad.gui;

import javafx.fxml.FXML;

public class ClientController {
    private String _addressIp;
    private int _port;
    private String _nickname;

    @FXML
    void initialize() {
        System.out.println("ClientController.init");
    }

    public void initData(String addressIp, int port, String nickname) {
        System.out.println("ClientController.initData");
        _addressIp = addressIp;
        _port = port;
        _nickname = nickname;
    }

    public void start() {
        System.out.println("ClientController.startServer: " + _nickname + " (" + _addressIp + ":" + _port + ")");
    }
}
