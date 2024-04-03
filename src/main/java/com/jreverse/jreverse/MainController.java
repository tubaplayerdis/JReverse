package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private final TreeItem emptyitem = new TreeItem("");

    @FXML
    private TreeView loadedClasTree;

    @FXML
    private void openPipMan() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("pipemanview.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("JReverse Pipe Manager");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void refreshClasses(){
        loadedClasTree.setRoot(emptyitem);
        TreeItem rootItem = new TreeItem("Loaded Classes");
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getLoadedClasses", JReverseBridge.NoneArg);
        for(String str : loadedclasses){
            rootItem.getChildren().add(new TreeItem(str));
        }
        loadedClasTree.setRoot(rootItem);
    }
}
