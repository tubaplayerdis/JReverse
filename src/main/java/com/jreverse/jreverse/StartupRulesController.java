package com.jreverse.jreverse;

import com.jreverse.jreverse.Utils.Developer;
import com.jreverse.jreverse.Utils.JReverseUtils;
import com.jreverse.jreverse.Utils.JReverseVersion;
import com.jreverse.jreverse.Utils.VersionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartupRulesController {
    //New Rules
    @FXML
    private TextArea NewRuleBytecodesPreviewBox;
    @FXML
    private TextField NewRuleNameField;

    //Old Rules
    @FXML
    private ListView<String> RulesListView;

    @FXML
    private TextArea OldRulePreviewBox;

    @FXML
    private TextField OldRuleNameField;

    @FXML
    private CheckBox NewRuleBypassCheckBox;

    @FXML
    private Label OldRuleBypassLabel;
    @FXML
    private Label noInternetErrorLabel;

    //Important
    public static List<StartupRule> rulesList = new ArrayList<>();

    //Version System
    public static VersionManager versionManager = null;

    public static StartupRule[] getRules(){
        StartupRule[] returnlist = rulesList.toArray(new StartupRule[0]);
        return  returnlist;
    }

    public void initialize(){
        RefreshRules();
        try {
            LoadSettings();
        } catch (IOException e) {
            System.out.println("Failed to load Settings");
        }
        Platform.runLater(() -> {
            //Current Version Stuff
            currentVersionLabel.setText("Downloaded Version: loading");
            //Loading...
            ObservableList<String> loadingitem = FXCollections.observableArrayList();
            loadingitem.add("Loading...");
            PublicVersionsListView.setItems(loadingitem);
            DevelopmentVersionsListView.setItems(loadingitem);

            //Developer Mode
            if(Developer.isDeveloperBuild) developerModeToggleButton.setVisible(true);
        });
        Thread newThread = new Thread(() -> {
            if(Objects.isNull(versionManager)) {
                versionManager = new VersionManager();
            }
            Platform.runLater(() -> {
                noInternetErrorLabel.setVisible(false);
                if(!versionManager.hasInternetConnection) {
                    noInternetErrorLabel.setVisible(true);
                }
                ObservableList<String> pubitems = FXCollections.observableArrayList();
                for(JReverseVersion version : versionManager.JReversePubList) {
                    pubitems.add(String.valueOf(version.version));
                }
                PublicVersionsListView.setItems(pubitems);

                ObservableList<String> devitems = FXCollections.observableArrayList();
                for(JReverseVersion version : versionManager.JReverseDevList) {
                    devitems.add(String.valueOf(version.version));
                }
                DevelopmentVersionsListView.setItems(devitems);

                //Version Stuff
                float versiondw = versionManager.GetDownloadedVersion();
                String versionstr = Float.toString(versiondw);
                if (versiondw == -2F) {
                    versionstr = "Error!";
                } else if (versiondw == -3F) {
                    versionstr = "None";
                }

                if(versionManager.isDownloadedLatest) {
                    versionstr = "Latest";
                }
                currentVersionLabel.setText("Downloaded Version: "+versionstr);
            });
        });
        newThread.start();
    }

    @FXML
    private void DeleteRule(){
        String CurrentRuleName = null;
        CurrentRuleName = RulesListView.getSelectionModel().getSelectedItem();
        if(CurrentRuleName.isBlank() || CurrentRuleName.isEmpty() || Objects.isNull(CurrentRuleName)) return;

        if(rulesList.isEmpty()) return;

        for(StartupRule rule : rulesList){
            if(Objects.equals(rule.ClassName, CurrentRuleName)) rulesList.remove(rule);
        }
    }

    //Old Rule Methods
    @FXML
    private void SelectOldRule(){
        String CurrentRuleName = null;
        CurrentRuleName = RulesListView.getSelectionModel().getSelectedItem();
        if(CurrentRuleName.isBlank() || CurrentRuleName.isEmpty() || Objects.isNull(CurrentRuleName)){
            OldRuleNameField.setText("Please Select a Rule!");
            OldRulePreviewBox.setText("Please Select a Rule!");
            return;
        }

        StartupRule currentRule = null;

        if(rulesList.isEmpty()) return;

        for(StartupRule rule : rulesList){
            if(Objects.equals(rule.ClassName, CurrentRuleName)) currentRule = rule;
        }

        if(Objects.isNull(currentRule)){
            OldRuleNameField.setText("Rule not found!");
            OldRulePreviewBox.setText("Rule not found!");
            return;
        }

        //Everything Successful
        OldRuleNameField.setText(currentRule.ClassName);
        String res = JReverseDecompiler.DecompileBytecodes(currentRule.ByteCodes);
        OldRulePreviewBox.setWrapText(false);
        OldRulePreviewBox.setText(res);
        OldRuleBypassLabel.setText("is bypass: "+currentRule.isBypass);

    }

    private String TempBytecodes = "";

    @FXML
    private void UploadBytecodes() throws IOException {
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Class Files", "class");
        j.setFileFilter(filter);
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);


        if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //Get Data of file
            File file = j.getSelectedFile();
            FileInputStream fis = new FileInputStream(file);
            StringBuilder hexBuilder = new StringBuilder();

            int data;
            while ((data = fis.read()) != -1) {
                // Convert each byte to hexadecimal representation
                String hex = Integer.toHexString(data);

                // Append leading zero if necessary
                if (hex.length() == 1) {
                    hexBuilder.append('0');
                }

                hexBuilder.append(hex);
            }

            fis.close();

            String hexString = hexBuilder.toString().toUpperCase();
            TempBytecodes = hexString;

            String res = JReverseDecompiler.DecompileBytecodes(hexString);
            NewRuleBytecodesPreviewBox.setText(res);
        }
    }

    @FXML
    private void CreateRule() {
        StartupRule create = new StartupRule();
        create.ClassName = NewRuleNameField.getText();
        create.isBypass = NewRuleBypassCheckBox.isSelected();

        create.ByteCodes = TempBytecodes;
        TempBytecodes = "";

        if(create.ClassName.isBlank() || create.ClassName.isEmpty() || create.ByteCodes.isBlank() || create.ByteCodes.isEmpty()){
            NewRuleBytecodesPreviewBox.setText("Error with bytecodes or class name");
            return;
        }

        //Add it
        rulesList.add(create);

        RefreshRules();
    }

    @FXML
    private void RefreshRules()
    {
        ObservableList<String> names = FXCollections.observableArrayList();
        for(StartupRule rule: rulesList){
            names.add(rule.ClassName);
        }
        RulesListView.setItems(names);
    }

    //Important
    @FXML
    private TabPane MainPane;

    //FXML stuff
    @FXML
    public CheckBox InjectOnStartupCheckBox;
    @FXML
    public CheckBox AutoStartCheckBox;
    @FXML
    public CheckBox ClassFileLoadMessagesCheckBox;
    @FXML
    public CheckBox ClassFileCollectionCheckBox;
    @FXML
    public CheckBox ConsoleWindowCheckBox;
    @FXML
    public Slider FuncLoopTimeoutSlider;
    @FXML
    public Slider JNIENVTimeoutSlider;

    @FXML
    public CheckBox DynamicClassFileCollectionCheckBox;
    @FXML
    public TextField DynamicClassFileCollectionPathTextArea;

    public static StartupSettings settings;

    public static final Path SettingsPath = Paths.get(System.getProperty("user.dir")+"/settings.xml");

    @FXML
    public void LoadSettings() throws IOException {
        settings = StartupSettingsHelper.CheckAndLoadFile();
        if(Objects.isNull(settings)) return;
        Platform.runLater(() -> {
            InjectOnStartupCheckBox.setSelected(settings.IsInjectOnStartup);
            AutoStartCheckBox.setSelected(settings.IsAutoStart);
            ClassFileLoadMessagesCheckBox.setSelected(settings.IsClassFileLoadMessages);
            ClassFileCollectionCheckBox.setSelected(settings.IsClassFileCollection);
            ConsoleWindowCheckBox.setSelected(settings.IsConsoleWindow);
            FuncLoopTimeoutSlider.setValue(settings.FuncLoopTimeout);
            JNIENVTimeoutSlider.setValue(settings.JNIEnvTimeout);
            DynamicClassFileCollectionCheckBox.setSelected(settings.IsDynamicClassFileCollection);
            DynamicClassFileCollectionPathTextArea.setText(settings.DynamicClassFileCollectionPath);
            DCFCCheckBoxToggle();
        });
    }
    @FXML
    public void SaveSettings() throws IOException {
        StartupSettings startupSettings = new StartupSettings();
        startupSettings.IsAutoStart = AutoStartCheckBox.isSelected();
        startupSettings.IsInjectOnStartup = InjectOnStartupCheckBox.isSelected();
        startupSettings.IsConsoleWindow = ConsoleWindowCheckBox.isSelected();
        startupSettings.IsClassFileLoadMessages = ClassFileLoadMessagesCheckBox.isSelected();
        startupSettings.IsClassFileCollection = ClassFileCollectionCheckBox.isSelected();
        startupSettings.FuncLoopTimeout = (int)FuncLoopTimeoutSlider.getValue();
        startupSettings.JNIEnvTimeout = (int)JNIENVTimeoutSlider.getValue();
        startupSettings.IsDynamicClassFileCollection = DynamicClassFileCollectionCheckBox.isSelected();
        startupSettings.DynamicClassFileCollectionPath = DynamicClassFileCollectionPathTextArea.getText();
        StartupSettingsHelper.WriteSettingsFile(startupSettings);
        LoadSettings();
    }

    @FXML
    private Button DCFCButton;
    @FXML
    private Label DCFCLabel;
    @FXML
    public void DCFCCheckBoxToggle() {
        DCFCButton.setDisable(!DynamicClassFileCollectionCheckBox.isSelected());
        DCFCLabel.setDisable(!DynamicClassFileCollectionCheckBox.isSelected());
        DynamicClassFileCollectionPathTextArea.setDisable(!DynamicClassFileCollectionCheckBox.isSelected());
    }

    @FXML
    public void SelectDCFCPath() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Set the file chooser to select directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog and capture the user's choice
        int returnValue = fileChooser.showDialog(null, "Select output directory");

        // If the user selects a folder
        String selectedFolderPath;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected folder
            selectedFolderPath = fileChooser.getSelectedFile().getPath();
            System.out.println("Selected folder: " + selectedFolderPath);
            DynamicClassFileCollectionPathTextArea.setText(selectedFolderPath);
        } else {
            System.out.println("No folder selected.");
            return;
        }
    }

    //Version System
    @FXML
    private ListView<String> PublicVersionsListView;
    @FXML
    private ListView<String> DevelopmentVersionsListView;
    @FXML
    private ListView<String> selectedVersionInfoListView;
    @FXML
    private Label currentVersionLabel;

    @FXML
    private void refreshVersionManager() {
        Platform.runLater(() -> {
            //Current Version Stuff
            currentVersionLabel.setText("Downloaded Version: loading");

            ObservableList<String> loadingitem = FXCollections.observableArrayList();
            loadingitem.add("Loading...");
            PublicVersionsListView.setItems(loadingitem);
            DevelopmentVersionsListView.setItems(loadingitem);
        });
        Thread newThread = new Thread(() -> {
            VersionManager manager = new VersionManager();
            Platform.runLater(() -> {
                noInternetErrorLabel.setVisible(false);
                if(!manager.hasInternetConnection) {
                    noInternetErrorLabel.setVisible(true);
                }
                versionManager = manager;
                ObservableList<String> pubitems = FXCollections.observableArrayList();
                for(JReverseVersion version : versionManager.JReversePubList) {
                    pubitems.add(String.valueOf(version.version));
                }
                PublicVersionsListView.setItems(pubitems);

                ObservableList<String> devitems = FXCollections.observableArrayList();
                for(JReverseVersion version : versionManager.JReverseDevList) {
                    devitems.add(String.valueOf(version.version));
                }
                DevelopmentVersionsListView.setItems(devitems);

                //Version Stuff
                float versiondw = versionManager.GetDownloadedVersion();
                String versionstr = Float.toString(versiondw);
                if (versiondw == -2F) {
                    versionstr = "Error!";
                } else if (versiondw == -3F) {
                    versionstr = "None";
                }

                if(versionManager.isDownloadedLatest) {
                    versionstr = "Latest";
                }
                currentVersionLabel.setText("Downloaded Version: "+versionstr);
            });
        });
        newThread.start();
    }
    @FXML
    private void getSelectedVersionInfoPub() {
        float versionnum = Float.parseFloat(PublicVersionsListView.getSelectionModel().getSelectedItem());
        JReverseVersion version = versionManager.GetVersionInfoByNum(versionnum, false);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("File Name: "+version.name);
        items.add("File Size(bytes): "+version.size);
        items.add("Date Created: "+version.date);
        items.add("Is Development Version: "+Boolean.toString(version.isdev));
        items.add("Download URL: "+version.downloadLink);
        selectedVersionInfoListView.setItems(items);
    }
    @FXML
    private void getSelectedVersionInfoDev() {
        float versionnum = Float.parseFloat(DevelopmentVersionsListView.getSelectionModel().getSelectedItem());
        JReverseVersion version = versionManager.GetVersionInfoByNum(versionnum, true);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("File Name: "+version.name);
        items.add("File Size(bytes): "+version.size);
        items.add("Date Created: "+version.date);
        items.add("Version Number: "+version.version);
        items.add("Is Development Version: "+Boolean.toString(version.isdev));
        items.add("Download URL: "+version.downloadLink);
        selectedVersionInfoListView.setItems(items);
    }

    @FXML
    private void SwitchVersion() {
        ObservableList<String> items = selectedVersionInfoListView.getItems();
        float versionnum = Float.parseFloat(items.get(3).substring(15, items.get(3).length()));
        System.out.println("Switching to version: "+versionnum);
        versionManager.SwitchVersion(versionnum);
        versionManager.Download();

        //Version Stuff
        float versiondw = versionManager.GetDownloadedVersion();
        String versionstr = Float.toString(versiondw);
        if (versiondw == -2F) {
            versionstr = "Error!";
        } else if (versiondw == -3F) {
            versionstr = "None";
        }

        if(versionManager.isDownloadedLatest) {
            versionstr = "Latest";
        }
        currentVersionLabel.setText("Downloaded Version: "+versionstr);
    }


    @FXML
    private Button developerModeToggleButton;
    @FXML
    private Pane toggleDeveloperMode;

    @FXML
    private void toggleDeveloperMode() {
        Developer.isDeveloperBuild = !Developer.isDeveloperBuild;
        if(Developer.isDeveloperBuild) {
            developerModeToggleButton.setVisible(true);
        } else {
            developerModeToggleButton.setVisible(false);
        }
    }

    @FXML
    private void showFileNeededWarning() {
        JReverseUtils.warningBox("Auto Start and Inject on startup will not work correctly if a JReverseCore.dll file is not downloaded. You can do this in the version manager or manually put a JReverseCore.dll at the same path as the jar", "Auto Start and Inject on Startup Warning");
    }
}
