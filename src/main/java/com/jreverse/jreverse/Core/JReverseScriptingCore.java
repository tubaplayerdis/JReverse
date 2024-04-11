package com.jreverse.jreverse.Core;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.io.StringWriter;

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

    public static String RunScript(String abpath) {
        if(Objects.isNull(engine)) return "Script Engine Was NULL";
        Path pathab = Paths.get(abpath);
        if(!Files.exists(pathab)) return "Script File Does Not Exist";
        String scripttext = null;
        try {
            scripttext = Files.readString(pathab);
        } catch (IOException e) {
            return "IOExeption: "+e.getMessage();
        }
        if(scripttext.isEmpty()) return "Script Text Is NULL";
        try{
            engine.eval(scripttext);
        } catch (ScriptException e){
            return "Script Error: "+e.getMessage();
        }
        return writer.toString();
    }
}
