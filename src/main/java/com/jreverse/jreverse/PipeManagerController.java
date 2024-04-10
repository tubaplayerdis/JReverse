package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Bridge.JReverseLogger;
import com.jreverse.jreverse.PipeManager.PipeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class PipeManagerController {
    @FXML
    private ListView loadedPipeView;

    @FXML
    private Button refreshButton;

    @FXML
    private void refreshPipes(){
        ObservableList<String> pipeStringList = FXCollections.observableArrayList();
        try{
            for(String i : PipeManager.GetLoadedPipes()){
                pipeStringList.add(i);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        loadedPipeView.setItems(pipeStringList);
    }

    @FXML
    private Button tesbutt;


    @FXML
    private void testfunp(){
        String[] what = JReverseBridge.CallCoreFunction("TESTFUNC", JReverseBridge.NoneArg);
        System.out.println(what[0] + " " + what[1]);
    }

    @FXML
    private Slider returnThreshSlider;

    @FXML
    private void setReturnThreshold(){
        JReverseLogger.PipeCallBackLimit = (int)returnThreshSlider.getValue();
    }

    @FXML
    private TextField getinfoField;

    @FXML
    private ListView<String> pipeInfoBox;

    @FXML
    private void getPipeInfo(){
        String[] pipinfo = PipeManager.GetPipeInfo(getinfoField.getText());
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Mode: "+pipinfo[0]);
        list.add("Name: "+pipinfo[1]);
        list.add("Size: "+pipinfo[2]);
        pipeInfoBox.setItems(list);
    }
}
