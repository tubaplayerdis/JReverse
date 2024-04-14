package com.jreverse.jreverse.Core;

import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

// ---------------------------------------------------------------
// COMPILE THIS FILE IN JAVA VERSION 1.8 FOR MAXIMUM COMPATIBILITY
// ---------------------------------------------------------------

public class JReverseScriptingCore {
    public static StringWriter writer = new StringWriter();

    public static int Main(){
        return 0;
    }
    public static String AddClass(String Classto){
        return "Development not needed";
    }

    public static String RunScript(String abpath) throws IOException {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.setOut(writer);

        if(Objects.isNull(interpreter)) return "Interpreter Engine Was NULL";

        // Check if the file exists
        if (!Files.exists(Paths.get(abpath))) {
            return "Script File Does Not Exist";
        }

        String scripttext = new String(Files.readAllBytes(Paths.get(abpath)));

        if(scripttext.isEmpty()) return "Script Text Is NULL";
        try{
            interpreter.exec(scripttext);
        } catch (Exception e){
            return "Interpreter Error: "+e.getMessage();
        }
        String res = writer.toString();
        return res;
    }
}
