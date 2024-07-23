package com.jreverse.jreverse;

import java.io.*;
import java.util.HexFormat;
import java.util.Objects;

public class JReverseDecompiler {
    public static final String usePath = System.getProperty("user.dir");
    public static String DecompileBytecodes(String bytecodes) {
        if(bytecodes.isEmpty() || bytecodes.isBlank() || Objects.isNull(bytecodes)) {
            return "Bytecodes were Empty!";
        }
        //Get Byte Array
        byte[] bytesofclass = HexFormat.of().parseHex(bytecodes.toUpperCase());

        //Write Bytes to file
        String tempfileabpath = null;
        try{
            File myFile = new File("temp" + ".class");
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(bytesofclass);
            fos.close();
            tempfileabpath = myFile.getAbsolutePath();
        } catch (IOException e){
            return "Error when writing to temp class file!";
        }

        if(tempfileabpath.isEmpty() || tempfileabpath.isBlank() || Objects.isNull(tempfileabpath)) {
            return "Temp File path was NULL";
        }

        String decompiledString = "Failed Decompilation!";

        switch (SettingsViewController.DecompOption){
            case CFR:
                try{
                    ProcessBuilder builder = new ProcessBuilder("java", "-jar", usePath+"\\libs\\cfr-0.152.jar", usePath+"\\temp.class");
                    Process process = builder.start();
                    //Read Data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if(decompiledString == "Failed Decompilation!") decompiledString="";
                        decompiledString = decompiledString+line+"\n";
                    }
                } catch (IOException e){
                    decompiledString = "Failed to read BufferedReader!";
                }
                break;
            case FERN_FLOWER:
                try{
                    ProcessBuilder builder = new ProcessBuilder("java", "-jar", usePath+"\\libs\\fernflower-2.5.0.Final.jar", usePath+"\\temp.class", "-");
                    Process process = builder.start();
                    //Read Data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if(decompiledString == "Failed Decompilation!") decompiledString="";
                        decompiledString = decompiledString+line+"\n";
                    }
                } catch (IOException e){
                    decompiledString = "Failed to read BufferedReader!";
                }
                break;
            case PROCYON:
                decompiledString = "PROCYON Decompiler not supported yet!";
                break;
            case JD_CORE:
                decompiledString = "JD_CORE Decompiler not supported yet!";
                break;
            case BYTECODE_VIEWER:
                decompiledString = "BYTECODE_VIEWER Decompiler not supported yet!";
                break;
        }

        return decompiledString;
    }
}
