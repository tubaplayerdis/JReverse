package com.jreverse.jreverse;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class startupController {

    @FXML
    private ListView procList ;

    @FXML
    private TextField PIDbox ;

    @FXML
    private TextArea targetField;

    ObservableList<String> procStringList = FXCollections.observableArrayList();

    int currentPID = -1;

    @FXML
    private void injectClick() throws IOException {
        if(currentPID == -1){
            return;
        }
        /*
        URL website = new URL("");
        try (InputStream in = website.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        */
        JReverseBridge.SetupPipe();
        JReverseBridge.InjectDLL(currentPID, "C:\\Users\\aaron\\source\\repos\\JReverseCore\\x64\\Debug\\JReverseCore.dll");
        Scene scene = new Scene(App.loadFXML("main"), 1280, 720);
        App.thestage.setResizable(false);
        App.thestage.setTitle("JReverse");
        App.thestage.setScene(scene);
        App.thestage.show();
        boolean test = JReverseBridge.testMethod();
        App.thestage.setResizable(test);
        System.out.println(JReverseBridge.GetStringPipe());
    }

    @FXML
    private void selectTarget(){
        Optional<ProcessHandle> prochan = ProcessHandle.of(Integer.parseInt(PIDbox.getText()));
        if(prochan.isEmpty()){
           return;
        }
        ProcessHandle processHandle = prochan.get();
        ProcessHandle.Info procinfo = processHandle.info();

        if(procinfo.command().isEmpty() || procinfo.user().isEmpty()){
            targetField.setText("The Process Handle Info was NULL");
            return ;
        }
        targetField.setText("PATH: "+procinfo.command().get()+"\nUSER: "+procinfo.user().get()+"\nPID: "+currentPID);
        currentPID = Integer.parseInt(PIDbox.getText());
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
        String line;
        Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /fo list");
        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            procStringList.add(line);//<-- Parse data here.
        }
        input.close();
        RefreshList();
    }


}

