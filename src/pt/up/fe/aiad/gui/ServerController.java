package pt.up.fe.aiad.gui;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pt.up.fe.aiad.utils.FXUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static jade.Boot.parseCmdLineArgs;

public class ServerController {
    private Stage _stage;
    private boolean _showGUI;

    @FXML
    private TextArea ta;

    @FXML
    void initialize() {
        System.out.println("ServerController.init");
    }

    public void initData(final Stage stage, boolean showGUI) {
        System.out.println("ServerController.initData");
        _stage = stage;
        _showGUI = showGUI;

        Console console = new Console(ta);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        //System.setErr(ps);
    }

    public void start() {
        System.out.println("ServerController.startServer");

        _stage.setOnCloseRequest(event -> {
            if (MainController.container != null) {
                try {
                    MainController.container.kill();
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

        MainController.container = Runtime.instance().createMainContainer(iae);
    }

    public static class Console extends OutputStream {

        private TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        @Override
        public void write(int i) throws IOException {
            output.appendText(String.valueOf((char) i));
        }
    }
}
