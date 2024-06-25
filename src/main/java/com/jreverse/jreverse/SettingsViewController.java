package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Bridge.JReverseLogger;
import com.tbdis.sstf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;


public class SettingsViewController {

    @FXML
    private Slider CallbackLimitSlider;

    @FXML
    private ChoiceBox<String> DecompilerChoiceBox;

    @FXML
    private ChoiceBox<String> LoggingLevChoiceBox;

    @FXML
    private CheckBox AVDModeCheckBox;

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

    public void setSettings() {
        File file = new File("settings.txt");
        System.out.println("Getting Settings!");
        //Get Settings
        Member[] settings = new Member[0];
        try {
            settings = Parser.ParseFile(file);
        } catch (ParserException e) {
            System.out.println("Error Parsing File!: "+e.getMessage());
        }
        if(settings.length < 4) {
            System.out.println("Invalid File! Creating Defaults");
            defaultSettingsFile();
            return;
        }

        /*
        0. CallBack
        1. Decompiler
        2. LoggingOptions
        3. Advanced Mode
         */
        JReverseLogger.PipeCallBackLimit = Integer.parseInt(settings[0].Data);
        SettingsViewController.DecompOption = SettingsViewController.getDecompilerOption(settings[1].Data);
        SettingsViewController.LoggingOption = SettingsViewController.getLoggingLevel(settings[2].Data);
        SettingsViewController.AVDMODE = Boolean.parseBoolean(settings[3].Data);
        System.out.println("setting gui changed to reflect settings");
        CallbackLimitSlider.setValue(JReverseLogger.PipeCallBackLimit);
        DecompilerChoiceBox.getSelectionModel().select(getDecompilerOptionString(DecompOption));
        LoggingLevChoiceBox.getSelectionModel().select(getLoggingOptionString(LoggingOption));
        AVDModeCheckBox.setSelected(AVDMODE);
    }

    public static void defaultSettingsFile() {
        File file = new File("settings.txt");
        Member[] members = new Member[4];
        members[0] = new Member("PCL","2000");
        members[1] = new Member("DCO","CFR");
        members[2] = new Member("LLO","ALL");
        members[3] = new Member("AVD","false");
        try {
            Writer.WriteFile(file, members);
        } catch (WriterException e) {
            System.out.println("Failed to write settings file!"+e.getMessage());
        }
    }

    public static void saveSettingsFile() {
        File file = new File("settings.txt");
        Member[] members = new Member[4];
        members[0] = new Member("PCL",String.valueOf(JReverseLogger.PipeCallBackLimit));
        members[1] = new Member("DCO",getDecompilerOptionString(DecompOption));
        members[2] = new Member("LLO",getLoggingOptionString(LoggingOption));
        members[3] = new Member("AVD",String.valueOf(AVDMODE));
        try {
            Writer.WriteFile(file, members);
        } catch (WriterException e) {
            System.out.println("Failed to write settings file!"+e.getMessage());
        }
    }

    public static void verifySettingsFile() {
        File file = new File("settings.txt");
        Member[] settings = new Member[0];
        try {
            settings = Parser.ParseFile(file);
        } catch (ParserException e) {
            System.out.println("Error Validating File: "+e.getMessage());
        }
        if(settings.length < 4) {
            System.out.println("Invalid File! Creating Defaults");
            //Create Defaults
            defaultSettingsFile();
            return;
        }

        /*
        0. CallBack
        1. Decompiler
        2. LoggingOptions
        3. Advanced Mode
         */
        try {
            Integer.parseInt(settings[0].Data);
        } catch (NumberFormatException e){
            defaultSettingsFile();
        }
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

        verifySettingsFile();
        setSettings();

        //Init options for DCFC Type
        ObservableList<String> InitExclusionTypesList = FXCollections.observableArrayList();
        InitExclusionTypesList.add("StartsWith");
        InitExclusionTypesList.add("Contains");
        InitExclusionTypesList.add("Both");
        DCFCTypeChoiceBox.setItems(InitExclusionTypesList);
        //Get Exclusions
        updateExclusionsFromCore();
    }

    public static DecompilerOption DecompOption = DecompilerOption.CFR;

    public static LoggingLevel LoggingOption = LoggingLevel.ALL;//TMI?

    public static boolean AVDMODE = false;


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

        //Save Settings
        saveSettingsFile();
    }

    private void saveSettings() {

    }

    //DCFC exclusions
    @FXML
    private ChoiceBox<String> DCFCTypeChoiceBox;
    @FXML
    private Label FeedbackLabel;
    @FXML
    private TextField ExclusionNameTextField;
    @FXML
    private ListView<String> ExclusionsListView;


    private int ChangeTypeToInt(String sus) {
        switch (sus) {
            case "StartsWith":
                return 0;
            case "Contains":
                return 1;
            case "Both":
                return 2;
            default:
                return 0;
        }
    }

    @FXML
    private void updateExclusionsFromButton() {
        updateExclusionsFromCore();
    }
    private void updateExclusionsFromCore() {
        String[] exs = JReverseBridge.CallCoreFunction("getExclusionNames", JReverseBridge.NoneArg);
        ObservableList<String> ExclusionList = FXCollections.observableArrayList();
        ExclusionList.addAll(exs);
        ExclusionsListView.setItems(ExclusionList);
    }
    @FXML
    private void removeSelected() {
        String[] args = {ExclusionsListView.getSelectionModel().getSelectedItem()};
        JReverseBridge.CallCoreFunction("removeExclusion", args);
        updateExclusionsFromCore();
    }
    @FXML
    private void modifySelected() {
        FeedbackLabel.setVisible(false);
        String[] args = {ExclusionNameTextField.getText(), String.valueOf(ChangeTypeToInt(DCFCTypeChoiceBox.getValue()))};
        String[] ret = JReverseBridge.CallCoreFunction("modifyExclusion", args);
        if(!ret[0].equals("Sucsessfully Modified Exclusion")) {
            FeedbackLabel.setVisible(true);
            FeedbackLabel.setText(ret[0]);
        }
        updateExclusionsFromCore();
    }
    @FXML
    private void addSelected() {
        FeedbackLabel.setVisible(false);
        String[] args = {ExclusionNameTextField.getText(), String.valueOf(ChangeTypeToInt(DCFCTypeChoiceBox.getValue()))};
        String[] ret = JReverseBridge.CallCoreFunction("addExclusion", args);
        if(!ret[0].equals("Sucsessfully Added Exclusion")) {
            FeedbackLabel.setVisible(true);
            FeedbackLabel.setText(ret[0]);
        }
        updateExclusionsFromCore();
    }

    @FXML
    private void selectExclusion() {
        String[] args = {ExclusionsListView.getSelectionModel().getSelectedItem()};
        String[] ret = JReverseBridge.CallCoreFunction("getExclusionInfo", args);
        ExclusionNameTextField.setText(ret[0]);
        DCFCTypeChoiceBox.getSelectionModel().select(Integer.parseInt(ret[1]));
    }
}
