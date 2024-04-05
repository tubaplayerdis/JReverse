package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private final TreeItem<String> emptyitem = new TreeItem<String>("");

    @FXML
    private TreeView<String> loadedClasTree;

    @FXML
    private ListView<String> FieldListView;

    @FXML
    private ListView<String> MethodListView;


    @FXML
    private TextField InstacneInfoBox;


    public void initialize() {

    }

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

    private void addClass(TreeItem<String> parent, String className) {
        String[] parts = className.split("/");
        TreeItem<String> currentParent = parent;
        for (String part : parts) {
            TreeItem<String> child = findChild(currentParent, part);
            if (child == null) {
                child = new TreeItem<>(part);
                currentParent.getChildren().add(child);
            }
            currentParent = child;
        }
    }

    private TreeItem<String> findChild(TreeItem<String> parent, String value) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (child.getValue().equals(value)) {
                return child;
            }
        }
        return null;
    }


    @FXML
    private void refreshClasses(){
        loadedClasTree.setRoot(emptyitem);
        TreeItem<String> rootItem = new TreeItem<String>(startupController.procName);
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getLoadedClasses", JReverseBridge.NoneArg);
        for(String str : loadedclasses){
            if(str.contains("[")) str = str.replace("[","");
            if(str.contains(";")) str = str.replace(";", "");
            str = str.replaceFirst("L", "");
            if(str.length() != 1) addClass(rootItem, str);
        }
        loadedClasTree.setRoot(rootItem);
    }

    @FXML
    private void populateClassInfo(){
        //Deconstuct Tree into String
        StringBuilder classpath = new StringBuilder();
        TreeItem<String> selected = loadedClasTree.getSelectionModel().getSelectedItem();
        while (selected != null){
            if(selected.getValue() == startupController.procName) break;
            classpath.insert(0, selected.getValue()+"/");
            selected = selected.getParent();
        }
        if(classpath.lastIndexOf("/") != -1) classpath.deleteCharAt(classpath.length()-1);
        if(classpath.length() < 2) return;
        System.out.println(classpath.toString());
        //Define Class String
        String[] ClassArgs = {classpath.toString()};

        //Populate Fields
        String[] Fields = JReverseBridge.CallCoreFunction("getClassFields", ClassArgs);
        ObservableList<String> FieldList = FXCollections.observableArrayList();
        FieldList.addAll(Fields);
        FieldListView.setItems(FieldList);
        System.out.println(Fields[0]);

        //Populate Methods
        String[] Methods = JReverseBridge.CallCoreFunction("getClassMethods", ClassArgs);
        ObservableList<String> MethodList = FXCollections.observableArrayList();
        MethodList.addAll(Methods);
        MethodListView.setItems(MethodList);
        System.out.println(Methods[0]);

        //Populate Instances
        String[] Instances = JReverseBridge.CallCoreFunction("getClassInstances", ClassArgs);
        System.out.println(Instances[0]);
        ObservableList<String> InstanceList = FXCollections.observableArrayList();
        InstanceList.addAll(Instances);
        //Add to info
        InstacneInfoBox.setText(Instances[Instances.length-1]);
        System.out.println(Instances[0]);
    }

    @FXML
    private void OpenScripterScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("script.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = new Stage();
        stage.setTitle("JReverse Script View");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void FieldSelected(){

    }
}

