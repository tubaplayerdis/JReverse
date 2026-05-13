package com.jreverse.jreverse.sstf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Writer {
    public static void WriteFile(String path, Member[] members) throws  WriterException {
        File file = new File(path);
        WriteFile(file, members);
    }
    public static void WriteFile(Path path, Member[] members) throws WriterException {
        File file = new File(String.valueOf(path));
        WriteFile(file, members);
    }
    public static void WriteFile(File file, Member[] members) throws WriterException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new WriterException("Unable to create File Writer: "+e.getMessage());
        }

        try {
            for (Member member : members){
                writer.write(member.Name+"│"+member.Data);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new WriterException("Unable to write file: "+e.getMessage());
        }


        try {
            writer.close();
        } catch (IOException e) {
            throw new WriterException("Unable to close File Writer: "+e.getMessage());
        }

    }
}
