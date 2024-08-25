package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ClassLoaderViewController {
    @FXML
    private Pane DragAndDropChoosePane;

    @FXML
    private ListView<String> FileListListView;

    @FXML
    private Button LoadFilesButton;

    @FXML
    private Pane HelpPane;

    @FXML
    private void ChooseAndUploadFile() {

    }

    @FXML
    private void DragOverEvent(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        //For file validation
    }

    @FXML
    private void DropEvent(DragEvent event) {
        //Files should be only dropped
        List<File> droppedfileslist = event.getDragboard().getFiles();
        ObservableList<String> FileTempList = FileListListView.getItems();
        for(File file : droppedfileslist) {
            System.out.println("Extension: "+file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()));
            switch(file.getName().substring(file.getName().lastIndexOf("."), file.getName().length())){
                case ".class":
                    break;
                case ".jar":
                    break;
                default:
                    return;
            }
            FileTempList.add(file.toString());
        }
        FileListListView.setItems(FileTempList);
    }

    @FXML
    private void DeleteSelectedEntry() {
        String deleteItem = FileListListView.getSelectionModel().getSelectedItem();
        ObservableList<String> replist = FileListListView.getItems();
        replist.remove(deleteItem);
        FileListListView.setItems(replist);
    }

    @FXML
    private void LoadFilesOnApp() {
        //Validate Validity of files here
        ObservableList<String> filestoadd = FileListListView.getItems();
        if (filestoadd.isEmpty()){
            System.out.println("No Files!");
            return;
        }
        //Fix COncurrentModificationException
        //Only can load a file at a time?
        ObservableList<String> reslist = filestoadd;
        for (String file : filestoadd){
            System.out.println("Adding File: "+file);
            if(!Files.exists(Paths.get(file))) { continue; }
            String[] args = {file};
            String[] res = JReverseBridge.CallCoreFunction("addPath", args);
            if(res[0].equals("Sucsess: ")) reslist.remove(file);
            //all files left on res list have errors
        }
        FileListListView.setItems(reslist);
    }

    @FXML
    private void ToggleHelpPane() {
        HelpPane.setVisible(!HelpPane.isVisible());
    }

}
