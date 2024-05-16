package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.util.ArrayList;
import java.util.Arrays;

public class SourceCodeDumpViewController {
    //Standard Exclusions
    @FXML
    private CheckBox SunMicrosystemsExclusionCheckBox;
    @FXML
    private CheckBox JavaLanguagesExclusionCheckBox;
    @FXML
    private CheckBox JavafxExclusionCheckBox;
    @FXML
    private CheckBox JavaxExclusionCheckBox;
    @FXML
    private CheckBox JDKExclusionCheckBox;


    //Custom Exclusions
    @FXML
    private ListView<String> CustomExclusionsListView;
    @FXML
    private TextField CustomExclusionTextField;

    //Options
    @FXML
    private TextField OutputDirectoryField;
    @FXML
    private CheckBox RefactorMissingClassBytecodesCheckBox;
    @FXML
    private CheckBox ResolveAllUnknownBytecodes;

    //Main
    @FXML
    private ProgressBar SouceCodeDumpProgressBar;

    @FXML
    private void AddCustomExclusionKeyRelease(KeyEvent event) {
        String key = event.getCode().toString();
        if(!key.equals("ENTER")) return;

        ObservableList<String> items = CustomExclusionsListView.getItems();
        items.add(CustomExclusionTextField.getText());
        CustomExclusionsListView.setItems(items);
        CustomExclusionsListView.refresh();
    }

    @FXML
    private void DeleteSelectedCustomExclusion() {
        String selectedItem = CustomExclusionsListView.getSelectionModel().getSelectedItem();
        ObservableList<String> items = CustomExclusionsListView.getItems();
        items.remove(selectedItem);
        CustomExclusionsListView.setItems(items);
        CustomExclusionsListView.refresh();
    }

    @FXML
    private void SelectOutputDirectory() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Set the file chooser to select directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog and capture the user's choice
        int returnValue = fileChooser.showDialog(null, "Select output directory for source code");

        // If the user selects a folder
        String selectedFolderPath;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected folder
            selectedFolderPath = fileChooser.getSelectedFile().getPath();
            System.out.println("Selected folder: " + selectedFolderPath);
            OutputDirectoryField.setText(selectedFolderPath);
        } else {
            System.out.println("No folder selected.");
            return;
        }
    }

    private String[] FilterClasses() {
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getLoadedClasses", JReverseBridge.NoneArg);
        ArrayList<String> JNINamedClasses = new ArrayList<>();
        Arrays.sort(loadedclasses);
        for (String str : loadedclasses) {
            if (str.contains("[")) str = str.replace("[", "");
            if (str.contains(";")) str = str.replace(";", "");
            str = str.replaceFirst("L", "");
            if (str.length() != 1) JNINamedClasses.add(str);
        }
        for(int i = 0; i < JNINamedClasses.size(); i++)
        {
            if(SunMicrosystemsExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("sun")) JNINamedClasses.remove(i);
            if(JavaLanguagesExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("java")) JNINamedClasses.remove(i);
            //Finish Rest
        }
    }

    @FXML
    private void DumpSourceCodeClick() {
        /*
        Pre Dump
        1.Calculate which classes to dump in a list factoring in exclusions
        2.Validate Output Directory
        During Dump
        1. Create Runnable for async
        2. Reserve Function and Return Pipe Access to source code dump activities and inform user with message
        3. Refactor All Classes if selected
        4. Resolve All Classes if selected
        5. Dump Source Code to relative directories
        6. Display Finished Message
        Notes:
        *Use Platform.runLater() to inform progress bar of progress.
        *Inform Progress Bar whenever possible.
        Progress Chunks:
        0% - 5% Setup
        5% - 45% Refactor Classes
        45% - 50% Resolving Classes
        50% - 100% Dumping Classes
         */
    }
}
