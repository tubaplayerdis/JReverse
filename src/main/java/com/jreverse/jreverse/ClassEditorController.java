package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javassist.bytecode.ClassFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ClassEditorController {
    @FXML
    private TreeView<String> ByteCodedClassesTreeView;

    @FXML
    private TextField CurrentClassTextField;

    @FXML
    private TextArea ClassDecompArea;

    private final TreeItem<String> emptyitem = new TreeItem<String>("");

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
    private void RefreshClasses(){
        ByteCodedClassesTreeView.setRoot(emptyitem);
        TreeItem<String> rootItem = new TreeItem<String>("Bytecoded Classes");
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getClassFileNames", JReverseBridge.NoneArg);
        Arrays.sort(loadedclasses);
        for (String str : loadedclasses) {
            if (str.length() != 1) addClass(rootItem, str);
        }
        ByteCodedClassesTreeView.setRoot(rootItem);
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

    @FXML
    private void ResolveClasses(){
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
    private void SelectClass() throws IOException {
        StringBuilder classpath = new StringBuilder();
        TreeItem<String> selected = ByteCodedClassesTreeView.getSelectionModel().getSelectedItem();
        while (selected != null) {
            if (selected.getValue() == "Bytecoded Classes") break;
            classpath.insert(0, selected.getValue() + "/");
            selected = selected.getParent();
        }
        if (classpath.lastIndexOf("/") != -1) classpath.deleteCharAt(classpath.length() - 1);
        if (classpath.length() < 2) return;


        String classname = classpath.toString();
        if(Objects.isNull(classname) || classname.isEmpty() || classname.isBlank() || Objects.equals(classname, "unknown")){
            CurrentClassTextField.setText("Please select a class that is not unknown or null");
            return;
        }

        CurrentClassTextField.setText(classname);

        //Decompile Class
        String[] ByteArgs = {classname};
        String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ByteArgs);
        ClassDecompArea.setWrapText(false);

        System.out.println(ClassByteCodes[1]);
        if (ClassByteCodes[1].equals("Class File Not Found")) {
            ClassDecompArea.setText("Class File for: "+classname+" was not found!");
            return;
        }

        final String usePath = System.getProperty("user.dir");
        //looks like: C:\Users\aaron\IdeaProjects\jreverse

        //Write the Data to temp.class;
        System.out.println("Decompiling BYTECODES: "+ClassByteCodes[1].toUpperCase());
        byte[] bytesofclass = HexFormat.of().parseHex(ClassByteCodes[1].toUpperCase());
        writeByteArrayToTempClassFile(bytesofclass);

        //Setup For Decomp
        String classFilePath = "temp.class"; // Path to your .class file

        System.out.println("Current Path: "+System.getProperty("user.dir"));


        String decompiledString = "Failed Decompilation!";

        //Cfr bs

        ProcessBuilder builder = new ProcessBuilder("java", "-jar", usePath+"\\libs\\cfr-0.152.jar", usePath+"\\temp.class");
        Process process = builder.start();

        // Read the output of the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            if(decompiledString == "Failed Decompilation!") decompiledString="";
            decompiledString = decompiledString+line+"\n";
        }

        ClassDecompArea.setText("Original Decomp of " + ClassByteCodes[0] + ":\n\n" + decompiledString);
    }
}
