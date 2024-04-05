package com.jreverse.jreverse;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.filechooser.*;
import java.io.FileReader;

public class ScriptViewController {

    private String scriptPath;

    @FXML
    private TextArea ScriptTextBox;

    @FXML
    private void LoadScriptFile() throws IOException {
        //Language is now javascript and its integrated scripting API with java:
        //https://docs.oracle.com/en/java/javase/11/scripting/java-scripting-api.html#GUID-BB128CF4-E0AE-487D-AF6C-3507AB186455
        //https://docs.oracle.com/en/java/javase/11/scripting/java-scripting-programmers-guide.pdf
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JavaScript Files", "js");
        j.setFileFilter(filter);
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);


        if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            //Get Text of file
            File file = j.getSelectedFile();
            FileReader fr = new FileReader(file.getAbsolutePath());
            String scripttext = "";

            int i;
            while ((i = fr.read()) != -1){
                scripttext+=(char)i;
            }
            //Set the text of the textarea;
            ScriptTextBox.setText(scripttext);
        }
    }

    //Rename Func Below
    @FXML
    private void RunPythonScript(){

    }

}
