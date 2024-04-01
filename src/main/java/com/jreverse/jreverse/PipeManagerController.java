package com.jreverse.jreverse;

import com.jreverse.jreverse.PipeManager.PipeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PipeManagerController {
    @FXML
    private ListView loadedPipeView;

    @FXML
    private Button refreshButton;

    private Boolean isInit = false;

    @FXML
    private void refreshPipes(){
        if(!isInit){
            PipeManager.InitAPI();
            isInit = true;
        }
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
}
