package com.jreverse.jreverse.Core;

import org.python.util.PythonInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


// ---------------------------------------------------------------
// COMPILE THIS FILE IN JAVA VERSION 1.8 FOR MAXIMUM COMPATIBILITY
// ---------------------------------------------------------------

public class JReverseScriptingCore {

    public static int Main(){
        return 0;
    }
    public static String AddClass(String Classto){
        return "Development not needed";
    }


    public static String RunScript(String abpath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(baos);
        try(PythonInterpreter interpreter = new PythonInterpreter()) {
            PrintStream old = System.out;
            //Set Stream
            System.setOut(stream);
            //Test Stream
            System.out.println("Execution of Script: "+abpath+"\n\n\n");

            if (Objects.isNull(interpreter)) return "Interpreter Engine Was NULL";

            // Check if the file exists
            if (!Files.exists(Paths.get(abpath))) {
                return "Script File Does Not Exist";
            }

            String scripttext = new String(Files.readAllBytes(Paths.get(abpath)));

            if (scripttext.isEmpty()) return "Script Text Is NULL";

            interpreter.exec(scripttext);


            String res = "Output: \n"+ stream.toString();

            //Return System.out
            System.setOut(old);

            return res;
        } catch (Exception e){
            return "PY Interpreter Error: "+e.getMessage();
        }
    }
}
