package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClassDumpDialogController {
    @FXML
    private ChoiceBox<String> SourceChoiceBox;

    @FXML
    private TextField classtodumpTextField;

    @FXML
    private TextField DumpLocationTextField;

    @FXML
    private CheckBox AttemptRefactorCheckBox;
    @FXML
    private Label MessageLabel;

    private String classtodump = MainController.CurrentClassName;
    public void initialize() {
        ObservableList<String> InitOptionsList = FXCollections.observableArrayList();
        InitOptionsList.add("another .class file");
        InitOptionsList.add("another .java file");
        InitOptionsList.add("source in text");
        SourceChoiceBox.setItems(InitOptionsList);
        SourceChoiceBox.getSelectionModel().select(2);
        if(classtodump.isEmpty() || classtodump.isBlank()) {
            classtodumpTextField.setText("Select a class and reopen this dialog!");
        }
        else {
            classtodumpTextField.setText(classtodump);
        }
    }


    @FXML
    private void SelectDumpLocation() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Set the file chooser to select directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog and capture the user's choice
        int returnValue = fileChooser.showDialog(null, "Select output directory for class dump");

        // If the user selects a folder
        String selectedFolderPath;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected folder
            selectedFolderPath = fileChooser.getSelectedFile().getPath();
            System.out.println("Selected folder for class dump: " + selectedFolderPath);
            DumpLocationTextField.setText(selectedFolderPath);
        } else {
            System.out.println("No folder selected.");
        }
    }

    //BUGS: Odd formating - shouold tab the data for better readability, not all info especially souce code not displaying with extra data, need better user information system
    @FXML
    private void DumpClass() throws IOException {
        if(classtodumpTextField.getText().equals("Select a class and reopen this dialog!") || classtodumpTextField.getText().isEmpty()) { MessageLabel.setText("Select a class and reopen this dialog!");  MessageLabel.setVisible(true); return;}
        if(DumpLocationTextField.getText().equals("No folder selected.") || DumpLocationTextField.getText().isEmpty()) { MessageLabel.setText("Select a dump location!"); MessageLabel.setVisible(true); return;}

        MessageLabel.setVisible(false);
        String bytecodes = "";

        if(AttemptRefactorCheckBox.isSelected()) {
            String[] args = {classtodump};
            JReverseBridge.CallCoreFunction("retransformClass", args);
            String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", args);
            if (ClassByteCodes[1].equals("Class File Not Found")) {
                bytecodes = "Refactor attempt failed!";
            }
            else {
                bytecodes = ClassByteCodes[1];
            }
        }

        switch(SourceChoiceBox.getSelectionModel().getSelectedItem()) {
            case "another .class file":
                try {
                    File myFile = new File(DumpLocationTextField.getText()+"\\"+classtodump + ".class");
                    FileOutputStream fos = new FileOutputStream(myFile);
                    fos.write(convertStringToByteArray(bytecodes.toUpperCase()));
                    fos.close();
                    System.out.println("Bytecode written to " + DumpLocationTextField.getText());
                } catch (IOException e) {
                    System.err.println("Error writing bytecode to file: " + e.getMessage());
                }
                break;
            case "another .java file":
                File myFile = new File(DumpLocationTextField.getText()+"\\"+classtodump + ".java");
                String source = JReverseDecompiler.DecompileBytecodes(bytecodes.toUpperCase());
                FileWriter writer = new FileWriter(myFile);
                writer.write(source);
                writer.close();
                break;
            default:
                break;
        }

        //Main Operations
        String[] ClassArgs = {classtodump};
        /*
        Methods
        Fields
        Instances
        Extra Info
        Source(if source as text)
         */

        //Create File
        String sanatize  = classtodump.replace("/", "_");
        System.out.println(DumpLocationTextField.getText()+"\\"+sanatize + "DUMP.txt");
        File dumpFile = new File(DumpLocationTextField.getText()+"/"+sanatize + "DUMP.txt");
        dumpFile.createNewFile();
        FileWriter writer = new FileWriter(dumpFile);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = now.format(formatter);
        writer.write("DUMP OF: "+classtodump+System.lineSeparator()+"DUMP TAKEN AT: "+dateTimeString+System.lineSeparator()+System.lineSeparator());

        //Populate Methods
        writer.write("METHODS: "+System.lineSeparator());
        String[] Methods = JReverseBridge.CallCoreFunction("getClassMethods", ClassArgs);
        WriteArray(Methods, writer);
        writer.write(System.lineSeparator());

        writer.write("FIELDS: "+System.lineSeparator());
        String[] Fields = JReverseBridge.CallCoreFunction("getClassFields", ClassArgs);
        WriteArray(Fields, writer);
        writer.write(System.lineSeparator());

        //Populate Instances
        String[] Instances = JReverseBridge.CallCoreFunction("getClassInstances", ClassArgs);
        writer.write("Instances at dump time: "+Instances[0]);
        writer.write(System.lineSeparator());

        System.out.println("Extra data on class: "+ClassArgs[0]);
        writer.write("EXTRA DATA: "+System.lineSeparator());
        String[] ExtraData = JReverseBridge.CallCoreFunction("getClassExtraData", ClassArgs);
        /*
         * IsInterface
         * Version
         * IsModifiable
         * IsArrayClass
         * Access Flags - https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html - These are already interpreted by the core
         * Status Int - https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#GetClassStatus  - These are already interpreted by the core
         */
        if(ExtraData.length >= 6)
        {
            writer.write("IsInterface: "+ExtraData[0]+System.lineSeparator());
            writer.write("Version: "+ExtraData[1]+System.lineSeparator());
            writer.write("IsModifiable: "+ExtraData[2]+System.lineSeparator());
            writer.write("IsInterface: "+ExtraData[3]+System.lineSeparator());
            writer.write("Access Flags: "+ExtraData[4]+System.lineSeparator());
            writer.write("Status Flags: "+ExtraData[5]+System.lineSeparator());
        }

        writer.write("SOURCE CODE: "+System.lineSeparator()+System.lineSeparator());
        if(SourceChoiceBox.getSelectionModel().getSelectedItem().equals("source in text")) {
            String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ClassArgs);
            if (ClassByteCodes[1].equals("Class File Not Found")) {
                writer.write("Refactor was not attempted or failed");
            } else {
                writer.write(JReverseDecompiler.DecompileBytecodes(ClassByteCodes[1]));
            }
        }

        writer.close();

    }

    private static byte[] convertStringToByteArray(String rawBytecodeString) {
        // Convert hexadecimal string to byte array
        int length = rawBytecodeString.length();
        byte[] bytecode = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytecode[i / 2] = (byte) ((Character.digit(rawBytecodeString.charAt(i), 16) << 4)
                    + Character.digit(rawBytecodeString.charAt(i + 1), 16));
        }
        return bytecode;
    }

    private void WriteArray(String[] array, FileWriter writer) {
        try {
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            //Unimportant
        }
        for(String str : array){
            try {
                writer.write("  "+str+System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Error writing array: " + e.getMessage());
                System.out.println("Continuing...");
            }
        }
    }
}
