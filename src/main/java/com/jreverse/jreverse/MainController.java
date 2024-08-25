package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Bridge.JReverseLogger;
import com.jreverse.jreverse.Debug.DebugConsoleViewController;
import com.jreverse.jreverse.Utils.JReverseUtils;
import com.tbdis.sstf.Member;
import com.tbdis.sstf.Parser;
import com.tbdis.sstf.ParserException;
import com.tbdis.sstf.WriterException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javassist.bytecode.ClassFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

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
    private CheckBox IsInterfaceCheckBox;

    @FXML
    private TextField ClassVersionInfoBox;

    @FXML
    private CheckBox IsModifyableCheckBox;
    @FXML
    private CheckBox IsArrayClassCheckBox;
    @FXML
    private TextField AccessInfoBox;
    @FXML
    private TextField StatusInfoBox;

    public static String CurrentClassName = "";

    public final String usePath = System.getProperty("user.dir");


    public void initialize() throws WriterException, ParserException {

        File file = new File("settings.txt");
        System.out.println("Getting Settings!");
        //Get Settings
        Member[] settings = Parser.ParseFile(file);
        if(settings.length < 4) {
            System.out.println("Invalid File! Creating Defaults");
            SettingsViewController.defaultSettingsFile();
            return;
        }

        /*
        0. CallBack
        1. Decompiler
        2. LoggingOptions
        3. Advanced Mode
         */
        JReverseLogger.PipeCallBackLimit = Integer.parseInt(settings[0].Data);
        SettingsViewController.DecompOption = SettingsViewController.getDecompilerOption(settings[1].Data);
        SettingsViewController.LoggingOption = SettingsViewController.getLoggingLevel(settings[2].Data);
        SettingsViewController.AVDMODE = Boolean.parseBoolean(settings[3].Data);

        Platform.runLater(() -> {
            String[] ver = JReverseBridge.CallCoreFunction("getVersionCore", JReverseBridge.NoneArg);
            if(JReverseBridge.GetVersion() != Float.parseFloat(ver[0])){
                JReverseUtils.warningBox(String.valueOf("Bridge and Core version mismatch! Some features might not work!\nBridge Version: "+JReverseBridge.GetVersion())+"\nCore Version: "+ver[0],"Version Mismatch!");
            }
        });
    }

    @FXML
    private void createAndOpenDebugConsoleView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Defunct/DebugConsoleView.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            DebugConsoleViewController controller = fxmlLoader.getController();
            Image image = new Image(usePath+"/icon/JReverseIcon.png");
            stage.getIcons().add(image);
            stage.setTitle("JReverse Debug Console");
            stage.setOnCloseRequest(e -> {
                controller.shutdown();
            });
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
            Image image = new Image(usePath+"/icon/JReverseIcon.png");
            stage.getIcons().add(image);
            stage.setTitle("JReverse Pipe Manager");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
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
            System.out.println("Bytecode written to temp.class");
            return myFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Error writing bytecode to file: " + e.getMessage());
        }
        return null;
    }

    private String[] FastclassList;
    @FXML
    private void refreshClasses() {
        loadedClasTree.setRoot(emptyitem);
        TreeItem<String> rootItem = new TreeItem<String>(startupController.procName);
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getLoadedClasses", JReverseBridge.NoneArg);
        FastclassList = loadedclasses;
        Arrays.sort(loadedclasses);
        for (String str : loadedclasses) {
            if (str.contains("[")) str = str.replace("[", "");
            if (str.contains(";")) str = str.replace(";", "");
            str = str.replaceFirst("L", "");
            if (str.length() != 1) addClass(rootItem, str);
        }
        loadedClasTree.setRoot(rootItem);
    }

    @FXML
    private TextField ClassNameFilterTextField;
    @FXML
    private void FilterClasses() {
        loadedClasTree.setRoot(emptyitem);
        TreeItem<String> rootItem = new TreeItem<String>(startupController.procName);
        String[] loadedclasses = FastclassList;
        Arrays.sort(loadedclasses);
        for (String str : loadedclasses) {
            if (str.contains("[")) str = str.replace("[", "");
            if (str.contains(";")) str = str.replace(";", "");
            str = str.replaceFirst("L", "");
            if (str.length() != 1 && str.contains(ClassNameFilterTextField.getText())) addClass(rootItem, str);
        }
        loadedClasTree.setRoot(rootItem);
    }

    @FXML
    private void populateClassInfo() throws IOException {
        //Deconstuct Tree into String
        StringBuilder classpath = new StringBuilder();
        TreeItem<String> selected = loadedClasTree.getSelectionModel().getSelectedItem();
        while (selected != null) {
            if (selected.getValue() == startupController.procName) break;
            classpath.insert(0, selected.getValue() + "/");
            selected = selected.getParent();
        }
        if (classpath.lastIndexOf("/") != -1) classpath.deleteCharAt(classpath.length() - 1);
        if (classpath.length() < 2) return;
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

        System.out.println("Extra data on class: "+ClassArgs[0]);
        String[] ExtraData = JReverseBridge.CallCoreFunction("getClassExtraData", ClassArgs);
        /*
         * IsInterface
         * Version
         * IsModifyable
         * IsArrayClass
         * Access Flags - https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html - These are already interpreted by the core
         * Status Int - https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#GetClassStatus  - These are already interpreted by the core
         */
        if(ExtraData.length >= 6)
        {
            IsInterfaceCheckBox.setSelected(Boolean.parseBoolean(ExtraData[0]));
            ClassVersionInfoBox.setText(ExtraData[1]);
            IsModifyableCheckBox.setSelected(Boolean.parseBoolean(ExtraData[2]));
            IsArrayClassCheckBox.setSelected(Boolean.parseBoolean(ExtraData[3]));
            AccessInfoBox.setText(ExtraData[4]);
            StatusInfoBox.setText(ExtraData[5]);
        }

        String[] ByteArgs = {MainController.CurrentClassName};
        String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ByteArgs);
        MethodDecompArea.setWrapText(false);

        System.out.println(ClassByteCodes[1]);
        if (ClassByteCodes[1].equals("Class File Not Found")) {
            MethodDecompArea.setText("Class File for: "+MainController.CurrentClassName+" was not found!");
            return;
        }

        final String usePath = System.getProperty("user.dir");
        //looks like: C:\Users\aaron\IdeaProjects\jreverse

        System.out.println("Current Path: "+System.getProperty("user.dir"));

        //Cfr bs
        String decompiledString = JReverseDecompiler.DecompileBytecodes(ClassByteCodes[1].toUpperCase());

        MethodDecompArea.setText("Decomp of " + ClassByteCodes[0] + ":\n\n" + decompiledString);
    }

    @FXML
    private void ResolveByteCodes() throws IOException {
        List<String> returnitems = new ArrayList<String>();
        String[] UnresolvedByteCodes = JReverseBridge.CallCoreFunction("getUnknownClassFiles", JReverseBridge.NoneArg);

        if(UnresolvedByteCodes[0].equals("NO CLASSES")) return;

        for (String unresolvedbytes : UnresolvedByteCodes) {
            //Check for file
            try {
                Files.deleteIfExists(Paths.get("temp.class"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Create and write to file
            byte[] bytesofclass = HexFormat.of().parseHex(unresolvedbytes.toUpperCase().replace(" ", ""));
            System.out.println((writeByteArrayToTempClassFile(bytesofclass)));

            //Open File
            File classFile = new File("temp.class");

            try (FileInputStream fis = new FileInputStream(classFile);
                 DataInputStream dis = new DataInputStream(fis)) {

                // Create a ClassFile object by reading the .class file
                ClassFile cf = new ClassFile(dis);

                // Get the class name
                String className = cf.getName().replace(".", "/");

                returnitems.add(className);
                returnitems.add(unresolvedbytes);

                System.out.println("Resolved: " + className);

                //Write back to the Core

            } catch (IOException e) {
                System.err.println("Error reading .class file: " + e.getMessage());
            }
        }
        System.out.println("Sending off class files");
        System.out.println("#of Resolved Classes: " + UnresolvedByteCodes.length);
        JReverseBridge.CallCoreFunction("setUnknownClassFiles", returnitems.toArray(new String[returnitems.size()]));
    }

    @FXML
    private void DumpClassFileNames() {
        String[] ClassFileNames = JReverseBridge.CallCoreFunction("getClassFileNames", JReverseBridge.NoneArg);
        Arrays.sort(ClassFileNames);
        StringBuilder builder = new StringBuilder();
        for (String str : ClassFileNames) {
            builder.append(str + "\n");
        }
        MethodDecompArea.setText(builder.toString());
    }

    @FXML
    private void OpenScripterScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("script.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 950, 700);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("JReverse Scripting Interface");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void RetransformClass() throws IOException {
        if(CurrentClassName.isEmpty() || CurrentClassName.isBlank())
        {
            System.out.println("Class Name Empty for Retransform!");
            return;
        }
        String[] args = {CurrentClassName};
        JReverseBridge.CallCoreFunction("retransformClass", args);
        String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", args);
        if (ClassByteCodes[1].equals("Class File Not Found")) {
            MethodDecompArea.setText("Class File for: "+MainController.CurrentClassName+" was not found!");
            return;
        }
        MethodDecompArea.setText(JReverseDecompiler.DecompileBytecodes(ClassByteCodes[1]));
    }
    @FXML
    private void KillJReverseSafe()
    {

    }

    @FXML
    private void FieldSelected() {

    }

    @FXML
    private void dumpClass() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ClassDumpDialog.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 489, 246);
        Stage stage = new Stage();
        stage.setResizable(false);
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.getIcons().add(image);
        stage.setTitle("JReverse Class Dump");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void OpenSourceCodeDumpView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("SouceCodeDumpView.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.getIcons().add(image);
        stage.setTitle("JReverse Source Code Dump");
        stage.setScene(scene);
        stage.show();
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
        if (!MethodListView.getSelectionModel().getSelectedItem().contains("(")) {
            MethodDecompArea.setText("Invalid Method");
        }

        String isstatic = "true";
        ObservableList<String> checklis = MethodListView.getItems();
        for (int i = 0; i < checklis.size(); i++) {
            if (checklis.get(i) == MethodListView.getSelectionModel().getSelectedItem()) break;
            if (checklis.get(i) == "NON STAIC") {
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
        String[] bytecodes = JReverseBridge.CallCoreFunction("getMethodBytecodes", args);

        //No More Work Needs to be done with this. move onto clas file load hooks and more advanced class modification
        MethodDecompArea.setWrapText(true);


        MethodDecompArea.setText(bytecodes[0]);
    }

    private static void createFoldersAndFile(String input, String base, byte[] data) throws IOException {
        // Split the input string by the last occurrence of "/"
        int lastSlashIndex = input.lastIndexOf("/");
        String foldersPath = base+ "/" + input.substring(0, lastSlashIndex);
        String fileName = input.substring(lastSlashIndex + 1);

        // Create folders
        File folders = new File(foldersPath);
        if (!folders.exists()) {
            folders.mkdirs();
        }

        // Create text file
        File filer = new File(foldersPath, fileName + ".class");
        if(!filer.exists()) {
            FileOutputStream fos = new FileOutputStream(filer);
            fos.write(data);
            fos.close();
            System.out.println("Wrote to: "+filer.getAbsolutePath());
        } else {
            System.out.println("File already exists: " + filer.getAbsolutePath());
        }

    }

    @FXML
    private void dumpSouce() throws IOException {

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Set the file chooser to select directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog and capture the user's choice
        int returnValue = fileChooser.showDialog(null, "Select Folder for source code");

        // If the user selects a folder
        String selectedFolderPath;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected folder
            selectedFolderPath = fileChooser.getSelectedFile().getPath();
            System.out.println("Selected folder: " + selectedFolderPath);
        } else {
            System.out.println("No folder selected.");
            return;
        }
        //Look like: C:\Users\aaron\OneDrive\Documents\NewFileTime

        String[] ClassFileNames = JReverseBridge.CallCoreFunction("getClassFileNames", JReverseBridge.NoneArg);
        System.out.println("Got Class File Names");
        int i = 0;
        for(String ClassName : ClassFileNames){
            if(ClassName.equals("unknown")){
                System.out.println("Class unknown. Skipping");
            } else {
                System.out.println("Getting Bytecodes of: "+ClassName);
                String[] ByteArgs = {ClassName};
                String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ByteArgs);
                byte[] bytesofclass = HexFormat.of().parseHex(ClassByteCodes[1].toUpperCase());
                createFoldersAndFile(ClassName, selectedFolderPath, bytesofclass);
                System.out.println("Wrote Bytecodes of: "+ClassName);
            }
            i++;
            System.out.println("Dump is "+ Math.round(((double) i/ClassFileNames.length)*10));
        }


    }

    @FXML
    private void OpenSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("SettingsView.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("JReverse Settings");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void OpenClassEditor() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ClassEditorView.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("JReverse Class Editor");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void OpenClassLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ClassLoaderView.fxml"));
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
        Scene scene = new Scene(fxmlLoader.load(), 393, 352);
        Stage stage = new Stage();
        Image image = new Image(usePath+"/icon/JReverseIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("JReverse Class Loader");
        stage.setScene(scene);
        stage.show();
    }
}
