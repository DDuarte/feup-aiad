package pt.up.fe.aiad.utils;

import org.controlsfx.dialog.Dialogs;

public class FXUtils {
    @SuppressWarnings("deprecation")
    static public void showExceptionDialog(Exception ex) {
        Dialogs.create()
                .owner(null)
                .title("Error | iScheduler")
                .masthead("Exception Encountered")
                .showException(ex);
    }
}
