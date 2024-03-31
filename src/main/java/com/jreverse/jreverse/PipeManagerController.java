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

    @FXML
    private void refreshPipes(){
        ObservableList<String> pipeStringList = FXCollections.observableArrayList();
        for(String i : PipeManager.GetLoadedPipes()){
            pipeStringList.add(i);
        }
        loadedPipeView.setItems(pipeStringList);
    }
}
