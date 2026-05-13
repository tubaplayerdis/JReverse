package com.jreverse.jreverse;


import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.PipeManager.PipeManager;
import com.jreverse.jreverse.Utils.Developer;
import com.jreverse.jreverse.Utils.JReverseUtils;
import com.jreverse.jreverse.Utils.VersionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.jreverse.jreverse.StartupRulesController.settings;


public class StartupController
{

    @FXML
    private ListView procList ;

    @FXML
    private TextField filterField ;

    @FXML
    private TextArea targetField;

    @FXML
    private ProgressBar JReverseCoreDownloadProgressBar;

    @FXML
    private VBox progressvbox;

    ObservableList<String> procStringList = FXCollections.observableArrayList();

    int currentPID = -1;

    String filterte = "";

    //second half of the jps command
    public static String procName = "";
    public static String procpath = "";

    int injectionreturn;

    boolean filtermode = true;


    public void initialize() {
        Platform.runLater(() -> {
            try {
                refreshProgList();
            } catch (IOException ignored) {

            }
        });
        Thread newThread = new Thread(() -> {
            StartupRulesController.versionManager = new VersionManager();
            StartupRulesController.isVersionManagerInit = true;
        });
        newThread.start();
    }
    @FXML
    private void OpenStartupRules() throws IOException {
        final String usePath = System.getProperty("user.dir");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("StartupRules.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 600, 430);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("JReverse Startup Settings");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void injectClick() throws IOException {
        //Use this for relative DLL when going to publish
        final String usePath = System.getProperty("user.dir");

        if(StartupRulesController.versionManager.GetDownloadedVersion() == -2F || StartupRulesController.versionManager.GetDownloadedVersion() == -3F && StartupRulesController.isVersionManagerInit) {
            System.out.println("Version Manager Detected no downloaded version. Downloading now.");
            if(!Developer.isDeveloperBuild) {
                progressvbox.setVisible(true);
                StartupRulesController.versionManager.Download();
            }
            else {
                System.out.println("Developer mode is enabled. resolving developer absolute for dll path");
            }
        }

        //looks like: C:\Users\aaron\IdeaProjects\jreverse
        //As in no backslash
        StartupSettings settings = StartupSettingsHelper.CheckAndLoadFile();
        if(Objects.isNull(settings)) {System.out.println("Startup Settings NULL"); return;}
        if(settings.IsInjectOnStartup && !App.isOnStartup) {WaitAndInject(settings.IsAutoStart); return;}

        JReverseBridge.testMethod();
        int resf = JReverseBridge.WriteStartupPipe(StartupRulesController.getRules(), settings);
        System.out.println("Wrote Startup: "+resf);
        PipeManager.InitAPI();
        System.out.println("Injecting!");


        //Add dev mode to continute debugging from this path in comparison to public where the version manager handles it
        String coredllpath = VersionManager.CoreDLLPath;
        //if(Developer.isDeveloperBuild) coredllpath = "C:\\Users\\aaron\\source\\repos\\JReverseCore\\x64\\Debug\\JReverseCore.dll";
        injectionreturn = JReverseBridge.InjectDLL(currentPID, coredllpath);
        Scene scene = new Scene(App.loadFXML("main"), 1280, 720);
        File style = new File(usePath+"/stylesheets/style.css");
        //scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        App.thestage.setResizable(false);
        App.thestage.setTitle("JReverse");
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        App.thestage.getIcons().add(image);
        App.thestage.setScene(scene);
        App.thestage.show();
        if(injectionreturn == 2) {
            JReverseUtils.infoBox("JReverse did not reinject the dll as it was already injected. startup data will be dismissed", "JReverse has already been injected");
        }
        //System.out.println(JReverseBridge.GetStringPipe());
    }

    private int GetPid(String jvmName){
        try {
            ProcessBuilder lipbuilder = new ProcessBuilder("jps");
            Process process = lipbuilder.start();
            // Read the output of the command
            BufferedReader reada = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String lina;
            while ((lina = reada.readLine()) != null) {
                if(lina.substring(lina.indexOf(" ")).equals(jvmName)) return Integer.parseInt(lina.substring(0, lina.indexOf(" ")));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @FXML
    private void selectTarget(){
        System.out.println(procList.getSelectionModel().getSelectedItem().toString());
        String selString = procList.getSelectionModel().getSelectedItem().toString();
        int PID = Integer.parseInt(selString.substring(0, selString.indexOf(" ")));
        System.out.println(PID);
        Optional<ProcessHandle> prochan = ProcessHandle.of(PID);
        if(prochan.isEmpty()){
           return;
        }
        ProcessHandle processHandle = prochan.get();
        ProcessHandle.Info procinfo = processHandle.info();

        if(procinfo.command().isEmpty() || procinfo.user().isEmpty()){
            targetField.setText("The Process Handle Info was NULL");
            return ;
        }
        currentPID = PID;
        if(!procinfo.command().get().isEmpty()){
            String raw = procinfo.command().get();
            String name = selString.substring(selString.indexOf(" "));
            targetField.setText("NAME: "+name+"\nPATH: "+procinfo.command().get()+"\nUSER: "+procinfo.user().get()+"\nPID: "+currentPID);
            StartupController.procName = selString.substring(selString.indexOf(" "));
            StartupController.procpath = procinfo.command().get();
            System.out.println(procinfo.command().get());
        }
    }

    private void RefreshList(){
        procList.refresh();
        procList.setItems(procStringList);
        procList.refresh();
    }


    @FXML
    private void refreshProgList() throws IOException {
        procStringList.clear();
        RefreshList();
        //Get java vm's
        if(filtermode) {
            ProcessBuilder lipbuilder = new ProcessBuilder("jps");
            Process procesr = lipbuilder.start();
            // Read the output of the command
            BufferedReader reada = new BufferedReader(new InputStreamReader(procesr.getInputStream()));
            String lina;
            while ((lina = reada.readLine()) != null) {
                procStringList.add(lina);
            }
            return;
        }
        RefreshList();
    }

    @FXML
    private void modFilter(){
        try{
            refreshProgList();
        } catch (IOException e){
            System.out.println("Prog List \"error\"");
        }


        filterte = filterField.getText();
    }

    @FXML
    private void WaitAndInject(boolean isAutoStart) {

        if(StartupController.procName.isEmpty()){
            System.out.println("procname is empty!");
            return;
        }
        if(StartupController.procpath.isEmpty()){
            System.out.println("procpath is empty!");
            return;
        }

        System.out.println("Killing: "+procName);
        //Kill The program
        try {
            ProcessBuilder killer = new ProcessBuilder("taskkill", "/f", "/t", "/pid", String.valueOf(currentPID)); // Modify this to match the name of your program
            Process killerProcess = killer.start();
            killerProcess.waitFor();

            while(GetPid(procName) != -1){
                System.out.println("Waiting for process kill");
            }



        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }


        //Auto Start
        if(isAutoStart){
            System.out.println("Auto Start True!");
            try {
                //Check for manual dll hotkey pressed
                /*
                KeyEvent event = new KeyEvent(null, 0, 0, KeyEvent.KEY_PRESSED, KeyEvent.VK_F2);
                int keyCode = event.getKeyCode();
                String dllpath = "NONE";
                if(keyCode == KeyEvent.VK_F2) {
                    //Manual DLL Selection
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnValue = fileChooser.showDialog(null, "Select DLL manually");
                    // If the user selects a folder
                    String selectedFolderPath;
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        // Get the selected folder
                        selectedFolderPath = fileChooser.getSelectedFile().getPath();
                        System.out.println("Selected file: " + selectedFolderPath);
                        dllpath = selectedFolderPath;
                    } else {
                        System.out.println("No file selected. Assuming download");
                    }
                }
                else {
                    //Respect Download Server option

                    //Download latest version

                    //Make proper changes to path
                }
                */


                final String usePath = System.getProperty("user.dir");

                StartupSettings settings = StartupSettingsHelper.CheckAndLoadFile();
                if(Objects.isNull(settings)) {System.out.println("Startup Settings NULL"); return;}


                int resf = JReverseBridge.WriteStartupPipe(StartupRulesController.getRules(), settings);
                PipeManager.InitAPI();
                System.out.println("Wrote Startup Code: " + resf);
                String coredllpath = VersionManager.CoreDLLPath;
                if(Developer.isDeveloperBuild) coredllpath = "C:\\Users\\aaron\\source\\repos\\JReverseCore\\x64\\Debug\\JReverseCore.dll";
                injectionreturn = JReverseBridge.InjectDLL(currentPID, coredllpath);
                Scene scene = new Scene(App.loadFXML("main"), 1280, 720);
                File style = new File(usePath + "/stylesheets/style.css");
                //scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
                App.thestage.setResizable(false);
                App.thestage.setTitle("JReverse");
                Image image = new Image(usePath + "/icon/JReverseIcon.png");
                App.thestage.getIcons().add(image);
                App.thestage.setScene(scene);
                App.thestage.show();
                if(injectionreturn == 2) {
                    JReverseUtils.infoBox("JReverse did not reinject the dll as it was already injected. startup data will be dismissed", "JReverse has already been injected");
                }
                return;
            } catch (IOException e){
                System.out.println("Error with FXML: "+e.getMessage());
            }
        }

        //Wait for restart
        Runnable myThread = () ->
        {
            while(GetPid(procName) == -1){
                //Wait for the proccess to get restarted
                System.out.println("Waiting for process restart");
            }

            //Callback to java fx thread and inject
            Platform.runLater(()->{
                App.isOnStartup = true;
                try {
                    injectClick();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
        };
        Thread run = new Thread(myThread);
        run.start();
        System.out.println(injectionreturn);

    }

    @FXML
    private Label ProcListLabel;

    @FXML
    private void SwitchFilterMode() {
        filtermode = !filtermode;
        if(filtermode){
            ProcListLabel.setText("Running JavaVM's:");
        } else {
            ProcListLabel.setText("Running Processes:");
        }
        try {
            refreshProgList();
        } catch (IOException e) {
            System.out.println("Error with refreshing list.");
        }
    }


}

