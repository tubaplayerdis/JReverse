package com.jreverse.jreverse;

import javafx.fxml.FXML;
import javax.swing.*;
import java.io.File;
import javax.swing.filechooser.*;

public class ScriptViewController {
    @FXML
    private void LoadScriptFile(){
        JFileChooser j = new JFileChooser();
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);

// Open the save dialog
        j.showSaveDialog(null);
    }
}
