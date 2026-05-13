package com.jreverse.jreverse.sstf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    public static Member[] ParseFile(String path) throws ParserException {
        File file = new File(path);
        return ParseFile(file);
    }

    public static Member[] ParseFile(Path path) throws ParserException {
        File file = new File(String.valueOf(path));
        return ParseFile(file);
    }

    public static Member[] ParseFile(File file) throws ParserException {
        if(!file.exists()) throw new ParserException("File does not exist!");

        FileReader reader = null;
        try{
            reader = new FileReader(file, StandardCharsets.UTF_8);
        } catch (IOException e){
            throw new ParserException("Unable to create File Reader: "+e.getMessage());
        }

        int lines = -1;
        try{
            lines = (int)Files.lines(Paths.get(file.getAbsolutePath())).count();
        } catch (IOException e){
            throw new ParserException("Unable to get number of lines in file: "+e.getMessage());
        }

        if(lines == -1){
            throw new ParserException("Number of lines invalid");
        }

        Member[] members = new Member[lines];

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if(line.indexOf("│") != line.lastIndexOf("│")) {
                    String nam = "inv";
                    String dat = "inv";
                    members[i] = new Setting(nam, dat);
                    i++;
                    continue;
                }
                if(!line.contains("│")) {
                    String nam = "inv";
                    String dat = "inv";
                    members[i] = new Setting(nam, dat);
                    i++;
                    continue;
                }
                String nam = line.substring(0, line.indexOf("│"));
                String dat = line.substring(line.indexOf("│")+1);
                members[i] = new Setting(nam, dat);
                i++;
            }
        } catch (IOException e) {
            throw new ParserException("Unable to Read File: "+e.getMessage());
        }

        try{
            reader.close();
        } catch (IOException e){
            throw new ParserException("Unable to close reader: "+e.getMessage());
        }

        return members;
    }

    public static Member[] ParseData(String[] lines) throws ParserException {
        ArrayList<Member> members = new ArrayList<>();
        for (String line : lines) {
            if(line.indexOf("│") != line.lastIndexOf("│")) {
                String nam = "inv";
                String dat = "inv";
                members.add(new Member(nam, dat));
                continue;
            }
            if(!line.contains("│")) {
                String nam = "inv";
                String dat = "inv";
                members.add(new Member(nam, dat));
                continue;
            }
            String nam = line.substring(0, line.indexOf("│"));
            String dat = line.substring(line.indexOf("│")+1);
            members.add(new Member(nam, dat));
        }
        return members.toArray(new Member[0]);
    }
}
