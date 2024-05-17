package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import com.jreverse.jreverse.Runtime.JReverseClassFile;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javassist.bytecode.ClassFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class SourceCodeDumpViewController {
    //Standard Exclusions
    @FXML
    private CheckBox SunMicrosystemsExclusionCheckBox;
    @FXML
    private CheckBox JavaLanguagesExclusionCheckBox;
    @FXML
    private CheckBox JavafxExclusionCheckBox;
    @FXML
    private CheckBox JavaxExclusionCheckBox;
    @FXML
    private CheckBox JDKExclusionCheckBox;
    @FXML
    private CheckBox LambdaExclusionCheckBox;


    //Custom Exclusions
    @FXML
    private ListView<String> CustomExclusionsListView;
    @FXML
    private TextField CustomExclusionTextField;

    //Options
    @FXML
    private TextField OutputDirectoryField;
    @FXML
    private CheckBox RefactorMissingClassBytecodesCheckBox;
    @FXML
    private CheckBox ResolveAllUnknownBytecodes;

    //Progress
    @FXML
    private ProgressBar SouceCodeDumpProgressBar;
    @FXML
    private Label ProgressLabel;

    //Warning Messages
    @FXML
    private Label WarningMessageLabel;
    @FXML
    private Label WarningLabel;

    @FXML
    private void AddCustomExclusionKeyRelease(KeyEvent event) {
        String key = event.getCode().toString();
        if(!key.equals("ENTER")) return;

        ObservableList<String> items = CustomExclusionsListView.getItems();
        items.add(CustomExclusionTextField.getText());
        CustomExclusionsListView.setItems(items);
        CustomExclusionsListView.refresh();
    }

    @FXML
    private void DeleteSelectedCustomExclusion() {
        String selectedItem = CustomExclusionsListView.getSelectionModel().getSelectedItem();
        ObservableList<String> items = CustomExclusionsListView.getItems();
        items.remove(selectedItem);
        CustomExclusionsListView.setItems(items);
        CustomExclusionsListView.refresh();
    }

    @FXML
    private void SelectOutputDirectory() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Set the file chooser to select directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog and capture the user's choice
        int returnValue = fileChooser.showDialog(null, "Select output directory for source code");

        // If the user selects a folder
        String selectedFolderPath;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected folder
            selectedFolderPath = fileChooser.getSelectedFile().getPath();
            System.out.println("Selected folder: " + selectedFolderPath);
            OutputDirectoryField.setText(selectedFolderPath);
        } else {
            System.out.println("No folder selected.");
            return;
        }
    }

    private String[] FilterClasses() {
        String[] loadedclasses = JReverseBridge.CallCoreFunction("getLoadedClasses", JReverseBridge.NoneArg);
        ArrayList<String> JNINamedClasses = new ArrayList<>();
        ArrayList<String> ReturnList = new ArrayList<>();
        Arrays.sort(loadedclasses);
        for (String str : loadedclasses) {
            if (str.contains("[")) str = str.replace("[", "");
            if (str.contains(";")) str = str.replace(";", "");
            str = str.replaceFirst("L", "");
            if (str.length() != 1) JNINamedClasses.add(str);
        }
        for(int i = 0; i < JNINamedClasses.size(); i++)
        {
            if(SunMicrosystemsExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("sun")) { continue; }
            if(SunMicrosystemsExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("com/sun")) { continue; }
            if(JavaLanguagesExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("java")) { continue; }
            if(JavafxExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("javafx")) { continue; }
            if(JavaxExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("javax")) { continue; }
            if(JDKExclusionCheckBox.isSelected() && JNINamedClasses.get(i).startsWith("jdk")) {  continue; }
            if(LambdaExclusionCheckBox.isSelected() && JNINamedClasses.get(i).contains("$Lambda$")) { continue; }


            for(String exclude : CustomExclusionsListView.getItems()) {
                if(JNINamedClasses.get(i).contains(exclude)) { continue; }
            }

            ReturnList.add(JNINamedClasses.get(i));
        }
        return ReturnList.toArray(new String[0]);
    }

    private static String[] filteredclasses = null;
    private static boolean isretrans = false;
    private static boolean isresolve = false;
    private static String outputpath = null;

    private static DecimalFormat df2 = new DecimalFormat("#.00");
    @FXML
    private void DumpSourceCodeClick() {
        /*
        Pre Dump
        1.Calculate which classes to dump in a list factoring in exclusions
        2.Validate Output Directory
        During Dump
        1. Create Runnable for async
        2. Reserve Function and Return Pipe Access to source code dump activities and inform user with message
        3. Refactor All Classes if selected
        4. Resolve All Classes if selected
        5. Dump Source Code to relative directories
        6. Display Finished Message
        Notes:
        *Use Platform.runLater() to inform progress bar of progress.
        *Inform Progress Bar whenever possible.
        Progress Chunks:
        0% - 5% Setup
        5% - 45% Refactor Classes
        50% - 100% Dumping Classes
         */



        WarningLabel.setVisible(true);
        WarningMessageLabel.setVisible(true);

        filteredclasses = FilterClasses();

        System.out.println("CLASSES TO DUMP:");
        for(String string : filteredclasses){
            System.out.println(string);
        }

        isretrans = RefactorMissingClassBytecodesCheckBox.isSelected();
        isresolve = ResolveAllUnknownBytecodes.isSelected();

        outputpath = OutputDirectoryField.getText();

        if(outputpath.isEmpty() || outputpath.isBlank() || !Files.exists(Path.of(outputpath))) return;

        Runnable runnable = () -> {
            Platform.runLater(() -> {
                ProgressLabel.setText("Progress: 5% - Setup");
            });


            if (isresolve) {
                List<String> returnitems = new ArrayList<String>();
                String[] UnresolvedByteCodes = JReverseBridge.CallCoreFunction("getUnknownClassFiles", JReverseBridge.NoneArg);

                if(!UnresolvedByteCodes[0].equals("NO CLASSES")) {

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
            }


            ArrayList<JReverseClassFile> preindexedclassfiles = new ArrayList<>();
            if (isretrans) {
                for(int i = 0; i < filteredclasses.length; i++) {
                    float finalI = i;
                    Platform.runLater(() -> {
                        double prog = ((((finalI/filteredclasses.length)*100)*0.45)+5);;
                        String currentprogess = df2.format(prog);
                        ProgressLabel.setText("Progress: "+currentprogess+"% - Retransforming Classes");
                    });
                    if(filteredclasses[i].isEmpty() || filteredclasses[i].isBlank())
                    {
                        System.out.println("Class Name Empty for Retransform!");
                        continue;
                    }
                    String[] args = {filteredclasses[i]};
                    String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", args);
                    if (!ClassByteCodes[1].equals("Class File Not Found")) {
                        preindexedclassfiles.add(new JReverseClassFile(filteredclasses[i],ClassByteCodes[1]));
                        continue;
                    }
                    JReverseBridge.CallCoreFunction("retransformClass", args);
                    String[] ClassByteCoders = JReverseBridge.CallCoreFunction("getClassBytecodes", args);
                    if (!ClassByteCoders[1].equals("Class File Not Found")) {
                        preindexedclassfiles.add(new JReverseClassFile(filteredclasses[i],ClassByteCoders[1]));
                    }
                }
            }

            //Main Work.
            if (isretrans) {
                int i = 0;
                for(JReverseClassFile ClassFile : preindexedclassfiles){
                    float finalI = i;
                    Platform.runLater(() -> {
                        double prog = ((((finalI/filteredclasses.length)*100)*0.50)+50);
                        String currentprogess = df2.format(prog);
                        ProgressLabel.setText("Progress: "+currentprogess+"% - Retransforming Classes");
                    });
                    if(ClassFile.Name.equals("unknown")){
                        System.out.println("Class unknown. Skipping");
                    } else {
                        byte[] bytesofclass = HexFormat.of().parseHex(ClassFile.Bytecodes);
                        try {
                            createFoldersAndFile(ClassFile.Name, outputpath, bytesofclass);
                        } catch (IOException e) {
                            System.out.println("Error with dump! aborting!");
                            return;
                        }
                    }
                    i++;
                }
            }
            else {
                String[] ClassFileNames = JReverseBridge.CallCoreFunction("getClassFileNames", JReverseBridge.NoneArg);
                System.out.println("Got Class File Names");
                int i = 0;
                for(String ClassName : ClassFileNames){
                    float finalI = i;
                    Platform.runLater(() -> {
                        double prog = (((finalI/filteredclasses.length)*100)*0.50)+50;
                        String currentprogess = df2.format(prog);
                        ProgressLabel.setText("Progress: "+currentprogess+"% - Retransforming Classes");
                    });
                    if(ClassName.equals("unknown")){
                        System.out.println("Class unknown. Skipping");
                    } else {
                        System.out.println("Getting Bytecodes of: "+ClassName);
                        String[] ByteArgs = {ClassName};
                        String[] ClassByteCodes = JReverseBridge.CallCoreFunction("getClassBytecodes", ByteArgs);
                        byte[] bytesofclass = HexFormat.of().parseHex(ClassByteCodes[1].toUpperCase());
                        try {
                            createFoldersAndFile(ClassName, outputpath, bytesofclass);
                        } catch (IOException e) {
                            System.out.println("Error with dump! aborting!");
                            return;
                        }
                        System.out.println("Wrote Bytecodes of: "+ClassName);
                    }
                    i++;
                }
            }
            Platform.runLater(() -> {
                WarningLabel.setVisible(false);
                WarningMessageLabel.setVisible(false);
            });

        };

        Thread thread1 = new Thread(runnable);
        thread1.start();
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
}
