package com.jreverse.jreverse;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

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
                Loader loader = new Loader() {
                    @Override
                    public byte[] load(String internalName) throws LoaderException {
                        InputStream is = this.getClass().getResourceAsStream("/" + internalName + ".class");

                        if (is == null) {
                            return null;
                        } else {
                            try (InputStream in=is; ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                                byte[] buffer = new byte[1024];
                                int read = in.read(buffer);

                                while (read > 0) {
                                    out.write(buffer, 0, read);
                                    read = in.read(buffer);
                                }

                                return out.toByteArray();
                            } catch (IOException e) {
                                throw new LoaderException(e);
                            }
                        }
                    }

                    @Override
                    public boolean canLoad(String internalName) {
                        return this.getClass().getResource("/" + internalName + ".class") != null;
                    }
                };

                Printer printer = new Printer() {
                    protected static final String TAB = "  ";
                    protected static final String NEWLINE = "\n";

                    protected int indentationCount = 0;
                    protected StringBuilder sb = new StringBuilder();

                    @Override public String toString() { return sb.toString(); }

                    @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}
                    @Override public void end() {}

                    @Override public void printText(String text) { sb.append(text); }
                    @Override public void printNumericConstant(String constant) { sb.append(constant); }
                    @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }
                    @Override public void printKeyword(String keyword) { sb.append(keyword); }
                    @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }
                    @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

                    @Override public void indent() { this.indentationCount++; }
                    @Override public void unindent() { this.indentationCount--; }

                    @Override public void startLine(int lineNumber) { for (int i=0; i<indentationCount; i++) sb.append(TAB); }
                    @Override public void endLine() { sb.append(NEWLINE); }
                    @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

                    @Override public void startMarker(int type) {}
                    @Override public void endMarker(int type) {}
                };

                ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
                try {
                    decompiler.decompile(loader, printer, usePath+"\\temp.class");
                } catch (Exception e) {
                    decompiledString = "Error Decompiling!";
                    break;
                }
                decompiledString = printer.toString();
                break;
            case BYTECODE_VIEWER:
                decompiledString = "BYTECODE_VIEWER Decompiler not supported yet!";
                break;
        }

        return decompiledString;
    }
}
