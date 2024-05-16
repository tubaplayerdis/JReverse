package com.jreverse.jreverse;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

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
    private void AddCustomExclusionKeyRelease() {

    }

    @FXML
    private void DeleteSelectedCustomExclusion() {

    }

    @FXML
    private void SelectOutputDirectory() {

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
