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
    private ListView<String> loadedPipeView;

    @FXML
    private Button refreshButton;

    @FXML
    private TextField GrowSizeTextField;

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
        //Add box to display this output
    }

    @FXML
    private Slider returnThreshSlider;

    @FXML
    private void setReturnThreshold(){
        JReverseLogger.PipeCallBackLimit = (int)returnThreshSlider.getValue();
        System.out.println("Set the Return Pipe Threshold to: "+(int)returnThreshSlider.getValue());
    }


    @FXML
    private ListView<String> pipeInfoBox;

    @FXML
    private void getPipeInfo() {
        System.out.println("Getting Pipe Data: "+loadedPipeView.getSelectionModel().getSelectedItem().toString().substring(0, loadedPipeView.getSelectionModel().getSelectedItem().toString().indexOf(":")));
        String[] pipinfo = PipeManager.GetPipeInfo(loadedPipeView.getSelectionModel().getSelectedItem().toString().substring(0, loadedPipeView.getSelectionModel().getSelectedItem().toString().indexOf(":")));
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Name: "+pipinfo[0]);
        list.add("Size: "+pipinfo[1]);
        list.add("Free Bytes: "+pipinfo[2]);
        pipeInfoBox.setItems(list);
        pipeInfoBox.refresh();
    }

    @FXML
    private TextField NewPipeSizeField;
    @FXML
    private void changePipeSize() {
        int newSize = 50000;
        try {
            newSize = Integer.parseInt(NewPipeSizeField.getText());
        } catch (NumberFormatException e){
            NewPipeSizeField.setText("Enter a valid number!");
            return;
        }
        PipeManager.ResizeAndReconnectPipe(loadedPipeView.getSelectionModel().getSelectedItem().toString().substring(0, loadedPipeView.getSelectionModel().getSelectedItem().toString().indexOf(":")), newSize);

    }

    @FXML
    private void GrowPipe() {
        int growsize = 1000;
        try {
            growsize = Integer.parseInt(GrowSizeTextField.getText());
        } catch (NumberFormatException e){
            GrowSizeTextField.setText("Enter a valid number!");
            return;
        }
        PipeManager.GrowPipe(loadedPipeView.getSelectionModel().getSelectedItem().toString().substring(0, loadedPipeView.getSelectionModel().getSelectedItem().toString().indexOf(":")), growsize);
    }
}
