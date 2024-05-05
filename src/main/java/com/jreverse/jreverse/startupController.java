package com.jreverse.jreverse;


import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.PipeManager.PipeManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;


public class startupController {

    @FXML
    private ListView procList ;

    @FXML
    private TextField filterField ;

    @FXML
    private TextArea targetField;

    ObservableList<String> procStringList = FXCollections.observableArrayList();

    int currentPID = -1;

    String filterte = "";

    public static String procName = "";
    public static String procpath = "";

    int injectionreturn;

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
        stage.setTitle("JReverse Startup Rules");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void injectClick() throws IOException {
        //Use this for relative DLL when going to publish
        final String usePath = System.getProperty("user.dir");
        //looks like: C:\Users\aaron\IdeaProjects\jreverse
        StartupSettings settings = StartupSettingsHelper.CheckAndLoadFile();
        if(Objects.isNull(settings)) {System.out.println("Startup Settings NULL"); return;}
        if(settings.IsAutoStart) {WaitAndInject(true); return;}

        currentPID = GetPid(procName);
        if(currentPID == -1){
            return;
        }
        JReverseBridge.testMethod();
        int resf = JReverseBridge.WriteStartupPipe(StartupRulesController.getRules());
        System.out.println("Wrote Startup: "+resf);
        PipeManager.InitAPI();
        injectionreturn = JReverseBridge.InjectDLL(currentPID, "C:\\Users\\aaron\\source\\repos\\JReverseCore\\x64\\Debug\\JReverseCore.dll");
        Scene scene = new Scene(App.loadFXML("main"), 1280, 720);
        File style = new File(usePath+"/stylesheets/style.css");
        //scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        App.thestage.setResizable(false);
        App.thestage.setTitle("JReverse");
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        App.thestage.getIcons().add(image);
        App.thestage.setScene(scene);
        App.thestage.show();
        //System.out.println(JReverseBridge.GetStringPipe());
    }

    private int GetPid(String processName){
        try {
            // Execute 'tasklist' command
            ProcessBuilder builder = new ProcessBuilder("tasklist", "/fo", "csv");
            Process process = builder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip the header line
            reader.readLine();

            // Search for the process by name and print its PID
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String processNameFromTasklist = parts[0].replaceAll("\"", ""); // Remove quotes
                if (processNameFromTasklist.equalsIgnoreCase(processName)) {
                    String pid = parts[1].replaceAll("\"", ""); // Remove quotes
                    return Integer.parseInt(pid);
                }
            }

            // Close the reader
            reader.close();

            // Wait for the process to finish
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @FXML
    private void selectTarget(){
        System.out.println(procList.getSelectionModel().getSelectedItem().toString());
        int PID = GetPid(procList.getSelectionModel().getSelectedItem().toString());
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
            String name = "";
            if(raw.lastIndexOf("\\") != -1){
                name = raw.substring(raw.lastIndexOf("\\")+1);
            }
            targetField.setText("NAME: "+name+"\nPATH: "+procinfo.command().get()+"\nUSER: "+procinfo.user().get()+"\nPID: "+currentPID);
            startupController.procName = name;
            startupController.procpath = procinfo.command().get();
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
        ProcessBuilder builder = new ProcessBuilder("tasklist", "/fo", "csv");
        Process process = builder.start();

        // Read the output of the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Skip the first three lines of the output (header)
        reader.readLine();
        reader.readLine();
        reader.readLine();

        // Print the running processes
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String processName = parts[0].replaceAll("\"", ""); // Remove quotes
            if (processName.toLowerCase().endsWith(".exe")) {
                if(line.contains(filterte)) {
                    String add = line.substring(0,line.indexOf(","));
                    procStringList.add(add.substring(1, add.length()-1));
                }
            }

        }

        // Close the reader
        reader.close();

        // Wait for the process to finish
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

        if(startupController.procName.isEmpty()){
            System.out.println("procname is empty!");
            return;
        }
        if(startupController.procpath.isEmpty()){
            System.out.println("procpath is empty!");
            return;
        }

        System.out.println("Killing: "+procName);
        //Kill The program
        try {
            ProcessBuilder killer = new ProcessBuilder("taskkill", "/f", "/im", procName); // Modify this to match the name of your program
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
            try {
                final String usePath = System.getProperty("user.dir");
                int resf = JReverseBridge.WriteStartupPipe(StartupRulesController.getRules());
                System.out.println("Wrote Startup: " + resf);
                JReverseBridge.StartAndInjectDLL("C:\\Users\\aaron\\source\\repos\\JReverseCore\\x64\\Debug\\JReverseCore.dll", procpath);
                Scene scene = new Scene(App.loadFXML("main"), 1280, 720);
                File style = new File(usePath + "/stylesheets/style.css");
                //scene.getStylesheets().add(style.toURI().toURL().toExternalForm());
                App.thestage.setResizable(false);
                App.thestage.setTitle("JReverse");
                Image image = new Image(usePath + "/icon/JReverseIcon.png");
                App.thestage.getIcons().add(image);
                App.thestage.setScene(scene);
                App.thestage.show();
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


}

