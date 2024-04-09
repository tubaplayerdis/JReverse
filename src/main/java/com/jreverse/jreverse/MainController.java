package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import javassist.bytecode.ClassFile;

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

    @FXML
    private Button ClassEditorButton;

    public static String CurrentClassName = "";




    public void initialize() {
        if(App.isOnStartup == true){
            ClassEditorButton.setDisable(false);
        } else {
            ClassEditorButton.setDisable(true);
        }
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

    private static byte[] convertStringToByteArray(String rawBytecodeString) {
        // Convert hexadecimal string to byte array
        int length = rawBytecodeString.length();
        byte[] bytecode = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytecode[i / 2] = (byte) ((Character.digit(rawBytecodeString.charAt(i), 16) << 4)
                    + Character.digit(rawBytecodeString.charAt(i + 1), 16));
        }
        return bytecode;
    }

    private static String writeByteArrayToTempClassFile(byte[] bytecode) {
        try {
            File myFile = new File("temp" + ".class");
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(bytecode);
            fos.close();
            System.out.println("Bytecode written to output.class");
            return myFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Error writing bytecode to file: " + e.getMessage());
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

        String[] ByteArgs = {MainController.CurrentClassName};
        String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ByteArgs);
        MethodDecompArea.setWrapText(true);
        MethodDecompArea.setText("Decomp of "+ClassByteCodes[0]+":\n\n"+ClassByteCodes[1].toUpperCase());

        //byte[] bytesofclass = HexFormat.of().parseHex(ClassByteCodes[1].toUpperCase().replace(" ",""));

        //System.out.println(writeByteArrayToTempClassFile(bytesofclass));
    }

    @FXML
    private void ResolveByteCodes() throws IOException {
        Runnable resolveThread = () -> {
            List<String> returnitems = new ArrayList<String>();
            String[] UnresolvedByteCodes = JReverseBridge.CallCoreFunction("getUnknownClassFiles", JReverseBridge.NoneArg);

            for(String unresolvedbytes : UnresolvedByteCodes){
                //Check for file
                try {
                    Files.deleteIfExists(Paths.get("temp.class"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(unresolvedbytes);

                //Create and write to file
                byte[] bytesofclass = HexFormat.of().parseHex(unresolvedbytes.toUpperCase().replace(" ",""));
                System.out.println((writeByteArrayToTempClassFile(bytesofclass)));

                //Open File
                File classFile = new File("temp.class");

                try (FileInputStream fis = new FileInputStream(classFile);
                     DataInputStream dis = new DataInputStream(fis)) {

                    // Create a ClassFile object by reading the .class file
                    ClassFile cf = new ClassFile(dis);

                    // Get the class name
                    String className = cf.getName().replace(".","/");

                    returnitems.add(className);
                    returnitems.add(unresolvedbytes);

                    System.out.println("Resolved: "+className);

                    //Write back to the Core

                } catch (IOException e) {
                    System.err.println("Error reading .class file: " + e.getMessage());
                }
            }
            System.out.println("Sending off class files");
            JReverseBridge.CallCoreFunction("setUnknownClassFiles", returnitems.toArray(new String[returnitems.size()]));
        };
        Thread run = new Thread(resolveThread);
        run.start();
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
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @FXML
    private void DecompileMethod() throws IOException {
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

        //No More Work Needs to be done with this. move onto clas file load hooks and more advanced class modification
        MethodDecompArea.setWrapText(true);


        MethodDecompArea.setText(bytecodes[0]);
    }
}

