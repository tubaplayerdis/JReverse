package com.jreverse.jreverse.Core;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class JReverseScriptingCore {
    public static ScriptEngine engine;
    public static int Main(){
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        if(Objects.isNull(engine)){
            return 1;
        }
        return 0;
    }

    public static String RunScript(String abpath) throws IOException, ScriptException {
        if(Files.exists(Paths.get(abpath)) != true) return "Script File Does Not Exist";
        String scripttext = new String(Files.readAllBytes(Paths.get(abpath)));
        if(scripttext.isEmpty() || scripttext == null) return "Script Text Is NULL";
        Object returnable;
        try{
            returnable = engine.eval(scripttext);
        } catch (ScriptException e){
            return "Script Error!"+e.getMessage();
        }
        return (String) returnable;
    }
}
