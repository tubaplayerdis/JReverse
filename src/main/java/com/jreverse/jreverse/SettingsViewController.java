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

    public static DecompilerOption getDecompilerOption(String value) {
        return switch (value) {
            case "CFR" -> DecompilerOption.CFR;
            case "JD_CORE" -> DecompilerOption.JD_CORE;
            case "FERN_FLOWER" -> DecompilerOption.FERN_FLOWER;
            case "PROCYON" -> DecompilerOption.PROCYON;
            case "BYTECODE_VIEWER" -> DecompilerOption.BYTECODE_VIEWER;
            default -> DecompilerOption.CFR;
        };
    }

    public static String getDecompilerOptionString(DecompilerOption option) {
        return switch (option) {
            case CFR -> "CFR";
            case JD_CORE -> "JD_CORE";
            case FERN_FLOWER -> "FERN_FLOWER";
            case PROCYON -> "PROCYON";
            case BYTECODE_VIEWER -> "BYTECODE_VIEWER";
        };
    }

    public static LoggingLevel getLoggingLevel(String value) {
        return switch (value) {
            case "LOW" -> LoggingLevel.LOW;
            case "MEDIUM" -> LoggingLevel.MEDIUM;
            case "HIGH" -> LoggingLevel.HIGH;
            case "ALL" -> LoggingLevel.ALL;
            case "NONE" -> LoggingLevel.NONE;
            default -> LoggingLevel.ALL;
        };
    }

    public static String getLoggingOptionString(LoggingLevel level) {
        return switch (level) {
            case ALL -> "ALL";
            case LOW -> "LOW";
            case MEDIUM -> "MEDIUM";
            case HIGH -> "HIGH";
            case NONE -> "NONE";
        };
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

    private void saveSettings() {

    }
}
