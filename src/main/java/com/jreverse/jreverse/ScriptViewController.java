package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Bridge.JReverseLogger;
import com.jreverse.jreverse.Core.JReverseScriptingCore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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
    private TextField CurrentIDBox;

    @FXML
    private ListView<String> InterpretersListView;

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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Python Files", "py");
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
        if(InterpretersListView.getItems().isEmpty()){
            ScriptOutputBox.setText("Create and select and interpreter!");
            return;
        }
        if(CurrentIDBox.getText().length() < 1){
            ScriptOutputBox.setText("No interpreter selected!");
            return;
        }
        //Run Script
        JReverseLogger.PipeCallBackLimit = 400;
        String[] args = {scriptPath, CurrentIDBox.getText()};
        String[] res = JReverseBridge.CallCoreFunction("runScript", args);
        if(res[0].equals("Interpreter was not found")) ScriptOutputBox.setText("Interpreter was not found");


    }


    @FXML
    private void setupScriptingEnv(){
        JReverseScriptingCore.Main();

        String[] args = {"No Bytecodes","No Bytecodes","No Bytecodes"};

        final String usePath = System.getProperty("user.dir");
        //looks like: C:\Users\aaron\IdeaProjects\jreverse
        args[0] = usePath+"\\src\\main\\java\\com\\jreverse\\jreverse\\Core\\com\\jreverse\\jreverse\\Core\\JReverseScriptingCore.class";
        args[1] = usePath+"\\libs\\jython-standalone-2.7.3.jar";
        args[1] = args[1].replace("\\","/");

        System.out.println(args[1]);

        JReverseLogger.PipeCallBackLimit = 2000;
        String res[] = JReverseBridge.CallCoreFunction("setupScriptingEnviroment", args);
        JReverseLogger.PipeCallBackLimit = 400;

        if(res[0].equals("1")) {ScriptOutputBox.setText("There was an error setting up the scripting environment:\n\n\n"+res[1]); return; }

        ScriptOutputBox.setText(res[0]);
        SetupScriptenvBut.setDisable(true);
        issetup = true;
    }

    @FXML
    private void updateOutput(){
        ScriptOutputBox.setText(JReverseBridge.CallCoreFunction("getStringWriterData", JReverseBridge.NoneArg)[0]);
    }

    @FXML
    private void flushOutput(){
        ScriptOutputBox.setText(JReverseBridge.CallCoreFunction("clearStringWriterData", JReverseBridge.NoneArg)[0]);
    }

    @FXML
    private void RefreshInterpreters(){
        if(!issetup){
            ScriptOutputBox.setText("Please setup the scripting environment!!");
            return;
        }
        ObservableList<String> updatelist = FXCollections.observableArrayList();
        String[] IDS = JReverseBridge.CallCoreFunction("getInterpreterIDS", JReverseBridge.NoneArg);
        updatelist.addAll(Arrays.asList(IDS));
        InterpretersListView.setItems(updatelist);
    }

    @FXML
    private void SelectInterpreter(){
        if(!issetup){
            ScriptOutputBox.setText("Please setup the scripting environment!!");
            return;
        }
        CurrentIDBox.setText(InterpretersListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void DeleteInterpreter(){
        if(!issetup){
            ScriptOutputBox.setText("Please setup the scripting environment!!");
            return;
        }
        String input = JOptionPane.showInputDialog("Enter the ID Number of the Interpreter to delete:");
        int id = -1;
        id = Integer.parseInt(input);
        if(id == -1){ JOptionPane.showMessageDialog(null, "Please Enter a Int value"); return;}
        String[] args = {input};
        JReverseBridge.CallCoreFunction("deleteInterpreter", args);
        RefreshInterpreters();
    }

    @FXML
    private void AddInterpreter(){
        if(!issetup){
            ScriptOutputBox.setText("Please setup the scripting environment!!");
            return;
        }
        String[] res = JReverseBridge.CallCoreFunction("createInterpreter", JReverseBridge.NoneArg);
        ScriptOutputBox.setText("Created Interpreter. ID: "+res[0]);
        RefreshInterpreters();
    }

}
