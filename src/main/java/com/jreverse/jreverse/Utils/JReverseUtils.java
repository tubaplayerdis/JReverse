package com.jreverse.jreverse.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class JReverseUtils {
    public static void infoBox(String infoMessage, String titleBar)
    {
        infoBox(infoMessage, titleBar, null, AlertType.INFORMATION);
    }

    public static void infoBox(String infoMessage, String titleBar, String headerMessage, AlertType type)
    {
        Alert alert = new Alert(type);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
    public static void warningBox(String infoMessage, String titleBar) {
        infoBox(infoMessage, titleBar, null, AlertType.WARNING);
    }
}
