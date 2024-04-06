package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

    @FXML
    private TextArea MethodDecompArea;

    public static String CurrentClassName = "";




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
        MainController.CurrentClassName = classpath.toString();

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
        InstacneInfoBox.setText(Instances[0]);
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

    @FXML
    private void dumpClass(){

    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @FXML
    private void DecompileMethod(){
        //use decompiler that supports raw bytecode. use CFR for class wide decompile in JReverseCore
        //Get Raw ByteCode
        if(!MethodListView.getSelectionModel().getSelectedItem().contains("(")){
            MethodDecompArea.setText("Invalid Method");
        }

        String isstatic = "true";
        ObservableList<String> checklis = MethodListView.getItems();
        for(int i = 0; i<checklis.size(); i++){
            if(checklis.get(i) == MethodListView.getSelectionModel().getSelectedItem()) break;
            if(checklis.get(i) == "NON STAIC"){
                isstatic = "false";
                break;
            }
        }

        String[] farg = MethodListView.getSelectionModel().getSelectedItem().split("\\(");
        String first = farg[0];
        String second = farg[1];
        StringBuilder builder = new StringBuilder(second);
        builder.insert(0, "(");
        second = builder.toString();
        String[] args = {MainController.CurrentClassName, first, second, isstatic};
        String[] bytecodes = JReverseBridge.CallCoreFunction("getMethodBytecodes",args);

        //Create or Write Method.class with data


        MethodDecompArea.setText(bytecodes[0]);
    }
}

