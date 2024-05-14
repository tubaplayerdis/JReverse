package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;


public class SettingsViewController {

    public enum LoggingLevel {
        LOW,
        MEDIUM,
        HIGH,
        ALL,
        NONE
    }

    public enum DecompilerOption {
        CFR,
        JD_CORE,
        FERN_FLOWER,
        PROCYON,
        BYTECODE_VIEWER
    }

    public void initialize() {
        ObservableList<String> InitOptionsList = FXCollections.observableArrayList();
        InitOptionsList.add("CFR");
        InitOptionsList.add("JD_CORE");
        InitOptionsList.add("FERN_FLOWER");
        InitOptionsList.add("PROCYON");
        InitOptionsList.add("BYTECODE_VIEWER");
        DecompilerChoiceBox.setItems(InitOptionsList);

        ObservableList<String> InitLevelList = FXCollections.observableArrayList();
        InitLevelList.add("LOW");
        InitLevelList.add("MEDIUM");
        InitLevelList.add("HIGH");
        InitLevelList.add("ALL");
        InitLevelList.add("NONE");
        LoggingLevChoiceBox.setItems(InitLevelList);

        //Load From Settings File
        /*
        Platform.runLater(() -> {
            Setting[] settings = null;
            try {
                settings = Parser.ParseSettings("runtimesettings");
            } catch (ParserException e) {
                //Assume defaults by doing nothing
            }
            for(Setting setting : settings){
                switch (setting.Name){
                    case "decompop":

                    default:
                        continue;
                }
            }
        });
         */
    }

    public static DecompilerOption DecompOption = DecompilerOption.CFR;

    public static LoggingLevel LoggingOption = LoggingLevel.ALL;//TMI?

    public static boolean AVDMODE = false;

    @FXML
    private Slider CallbackLimitSlider;

    @FXML
    private ChoiceBox<String> DecompilerChoiceBox;

    @FXML
    private ChoiceBox<String> LoggingLevChoiceBox;

    @FXML
    private CheckBox AVDModeCheckBox;

    @FXML
    private void ApplySettings(){
        //Set the Callback Limit
        JReverseLogger.PipeCallBackLimit = (int)CallbackLimitSlider.getValue();
        System.out.println("Set the Return Pipe Threshold to: "+(int)CallbackLimitSlider.getValue());

        //Apply the Decompiler Option
        switch (DecompilerChoiceBox.getSelectionModel().getSelectedItem()){
            case "CFR":
                DecompOption = DecompilerOption.CFR;
                break;
            case "JD_CORE":
                DecompOption = DecompilerOption.JD_CORE;
                break;
            case "FERN_FLOWER":
                DecompOption = DecompilerOption.FERN_FLOWER;
                break;
            case "PROCYON":
                DecompOption = DecompilerOption.PROCYON;
                break;
            case "BYTECODE_VIEWER":
                DecompOption = DecompilerOption.BYTECODE_VIEWER;
                break;
            default:
                DecompOption = DecompilerOption.CFR;
                break;
        }

        System.out.println("Set the Decompiler Option");

        //Apply the Logging Level
        switch (LoggingLevChoiceBox.getSelectionModel().getSelectedItem()){
            case "LOW":
                LoggingOption = LoggingLevel.LOW;
                break;
            case "MEDIUM":
                LoggingOption = LoggingLevel.MEDIUM;
                break;
            case "HIGH":
                LoggingOption = LoggingLevel.HIGH;
                break;
            case "ALL":
                LoggingOption = LoggingLevel.ALL;
                break;
            case "NONE":
                LoggingOption = LoggingLevel.NONE;
                break;
            default:
                LoggingOption = LoggingLevel.ALL;
                break;
        }

        AVDMODE = AVDModeCheckBox.isSelected();

        System.out.println("Set the Logging Level");
    }

    private void SetOptionsByString(String level, String decompop, String loggingop) {

    }
}
