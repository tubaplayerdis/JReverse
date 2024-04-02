package com.jreverse.jreverse;

import com.jreverse.jreverse.PipeManager.PipeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    //Add Pipe stuff

    @FXML
    private TextArea nameAddField;

    @FXML
    private ChoiceBox pipeTypeChoice;

    @FXML
    private TextArea sizeAddField;

    @FXML
    private Button addPipeButton;

    @FXML
    private  void populateChoices(){
        //Populate the ChoiceBox
        ObservableList<String> pipeOptionList = FXCollections.observableArrayList();
        pipeOptionList.add("int");
        pipeOptionList.add("bool");
        pipeOptionList.add("float");
        pipeOptionList.add("double");
        pipeOptionList.add("std::string");
        pipeOptionList.add("std::vector<std::string>");
        pipeTypeChoice.setItems(pipeOptionList);
    }

    @FXML
    private void addPipe(){
        String pipnam = nameAddField.getText();
        int pipsize = Integer.parseInt(sizeAddField.getText());
        String piptype = pipeTypeChoice.getSelectionModel().getSelectedItem().toString();

        PipeManager.AddPipe(pipnam, pipsize, piptype);
    }
}
