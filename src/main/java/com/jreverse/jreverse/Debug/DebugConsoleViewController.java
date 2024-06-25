package com.jreverse.jreverse.Debug;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.PrintStream;

public class DebugConsoleViewController {
    @FXML
    private TextArea ConsoleOutputTextArea;
    @FXML
    private ListView<String> IncidentArea;

    private static final File ouputfile = new File("output.log");
    private static PrintStream stream = new PrintStream(new DebugOutputStream(null));

    public static void Setup() {
        System.setOut(stream);
    }
    public void initialize() {
       //Not Conna Use THis

    }

    public void shutdown() {
        // cleanup code here...
    }
}
