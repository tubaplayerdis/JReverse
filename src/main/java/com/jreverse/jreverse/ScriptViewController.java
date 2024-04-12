package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Bridge.JReverseLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class ScriptViewController {

    private static Boolean issetup = false;
    private static String scriptPath;

    @FXML
    private TextArea ScriptTextBox;

    @FXML
    private Button SetupScriptenvBut;

    @FXML
    private TextArea ScriptOutputBox;

    @FXML
    private void initialize(){
        SetupScriptenvBut.setDisable(issetup);
    }

    private static File CurrentFile;

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

            scriptPath = file.getAbsolutePath();

            int i;
            while ((i = fr.read()) != -1){
                scripttext+=(char)i;
            }
            //Set the text of the textarea;
            ScriptTextBox.setText(scripttext);
            CurrentFile = file;
        }
    }

    @FXML
    private void UpdateScriptFile() throws IOException {
        if(Objects.isNull(CurrentFile)){
            ScriptTextBox.setText("");
            ScriptOutputBox.setText("Please choose a file via the button above!");
            return;
        }
        FileWriter CurrentFileWriter = new FileWriter(CurrentFile,false);
        CurrentFileWriter.write(ScriptTextBox.getText());
        CurrentFileWriter.close();
    }

    @FXML
    private void runScriptOnTarget() throws IOException {
        if(Objects.isNull(CurrentFile)){
            ScriptTextBox.setText("");
            ScriptOutputBox.setText("Please choose a file via the button above!");
            return;
        }
        if(!issetup){
            ScriptOutputBox.setText("Please setup the scripting environment!!");
            return;
        }
        //Run Script
        String[] args = {scriptPath};
        String[] res = JReverseBridge.CallCoreFunction("runScript", args);
        ScriptOutputBox.setText(res[0]);
    }

    @FXML
    private void setupScriptingEnv(){

        String[] args = {"No Bytecodes","No Bytecodes","No Bytecodes"};

        final String usePath = System.getProperty("user.dir");
        //looks like: C:\Users\aaron\IdeaProjects\jreverse
        args[0] = usePath+"\\src\\main\\java\\com\\jreverse\\jreverse\\Core\\JReverseScriptingCore.class";

        JReverseLogger.PipeCallBackLimit = 400;
        String res[] = JReverseBridge.CallCoreFunction("setupScriptingEnviroment", args);
        JReverseLogger.PipeCallBackLimit = 200;

        if(res[0].equals("1")) {ScriptOutputBox.setText("There was an error setting up the scripting environment:\n\n\n"+res[1]); return; }

        ScriptOutputBox.setText(res[0]);
        SetupScriptenvBut.setDisable(true);
        issetup = true;
    }

}
