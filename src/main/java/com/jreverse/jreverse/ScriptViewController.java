package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ScriptViewController {

    private String scriptPath;

    @FXML
    private TextArea ScriptTextBox;

    @FXML
    private void LoadScriptFile() throws IOException {
        //Language is now javascript and its integrated scripting API with java:
        //https://docs.oracle.com/en/java/javase/11/scripting/java-scripting-api.html#GUID-BB128CF4-E0AE-487D-AF6C-3507AB186455
        //https://docs.oracle.com/en/java/javase/11/scripting/java-scripting-programmers-guide.pdf
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JavaScript Files", "js");
        j.setFileFilter(filter);
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);


        if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            //Get Text of file
            File file = j.getSelectedFile();
            FileReader fr = new FileReader(file.getAbsolutePath());
            String scripttext = "";

            int i;
            while ((i = fr.read()) != -1){
                scripttext+=(char)i;
            }
            //Set the text of the textarea;
            ScriptTextBox.setText(scripttext);
        }
    }

    //Rename Func Below
    @FXML
    private void setupScriptingEnv(){

        String[] args = {"No Bytecodes","No Bytecodes","No Bytecodes"};

        ClassPool classPool = ClassPool.getDefault();
        String className = "com.jreverse.jreverse.Core.JReverseScriptingCore";
        try {
            // Get the CtClass representation of the class
            CtClass ctClass = classPool.get(className);

            // Retrieve the bytecode as a byte array
            byte[] bytecode = ctClass.toBytecode();

            args[0] = new String(bytecode);

            // Don't forget to close the CtClass when done
            ctClass.detach();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            System.out.println(e.getMessage());
        }

        String res[] = JReverseBridge.CallCoreFunction("setupScriptingEnviroment", args);

        System.out.println("Scripting Environment Setup Result: "+res[0]);
    }

}
