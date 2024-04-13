package com.jreverse.jreverse.Core;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
    public static ScriptEngine engine;
    public static int Main(){
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        if(Objects.isNull(engine)){
            return 1;
        }
        engine.getContext().setWriter(writer);
        return 0;
    }
    
    public static String AddClass(String Classto){
        try {
            Class<?> claz = Class.forName(Classto);
        } catch (ClassNotFoundException e){
            return e.getMessage();
        }

        return "Successfully added class";
    }

    public static String RunScript(String abpath) throws IOException {
        if(Objects.isNull(engine)) return "Script Engine Was NULL";

        // Check if the file exists
        if (!Files.exists(Paths.get(abpath))) {
            return "Script File Does Not Exist";
        }

        String scripttext = new String(Files.readAllBytes(Paths.get(abpath)));

        if(scripttext.isEmpty()) return "Script Text Is NULL";
        try{
            engine.eval(scripttext);
        } catch (Exception e){
            return "Script Error: "+e.getMessage();
        }
        return String.valueOf(writer.toString());
    }
}
