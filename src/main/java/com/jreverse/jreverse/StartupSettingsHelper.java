package com.jreverse.jreverse;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class StartupSettingsHelper {
    private static final Path SettingsPath = Paths.get(System.getProperty("user.dir")+"/settings.xml");

    public static boolean CheckSettingsFile() {
        System.out.println("Settings File Existence: "+Files.exists(SettingsPath));
        return Files.exists(SettingsPath);
    }

    private static void VerifySettings() {
        System.out.println("Verifying Settings File!");
        if(!CheckSettingsFile()){
            CreateSettingsFile();
        }
        //Load File
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException e){
            System.out.println("Could not Create Parser Instance! Error: "+e.getMessage());
            return;
        }

        if(Objects.isNull(builder)){
            System.out.println("Null builder");
            return;
        }

        //Parse data
        Document doc = null;
        try {
            doc = builder.parse(SettingsPath.toString());
        } catch (IOException | SAXException e){
            System.out.println("Document was null: "+e.getMessage());
            return;
        }

        // Step 3: Read Data
        // For example, let's print out all the <title> elements in the XML
        NodeList mainNodes = doc.getElementsByTagName("settings");

        File xmlFile = SettingsPath.toFile();
        StartupSettings settings = new StartupSettings();

        //Check null of first element
        Node settingsnode = mainNodes.item(0);
        if(Objects.isNull(settingsnode)){
            System.out.println("Settings node is empty!");
            return;
        }
        NamedNodeMap attributes = settingsnode.getAttributes();
        Node a = attributes.getNamedItem("autostart");
        Node b = attributes.getNamedItem("injectonstartup");
        Node c = attributes.getNamedItem("classfilecollection");
        Node d = attributes.getNamedItem("classfileloadmessages");
        Node e = attributes.getNamedItem("consolewindow");
        Node f = attributes.getNamedItem("funclooptimeout");
        Node g = attributes.getNamedItem("jnienvtimeout");
        Node h = attributes.getNamedItem("dymanicclassfilecollection");
        Node i = attributes.getNamedItem("dymanicclassfilecp");

        if (Objects.isNull(a) || Objects.isNull(b) || Objects.isNull(c) || Objects.isNull(d) || Objects.isNull(e) || Objects.isNull(f) || Objects.isNull(g) || Objects.isNull(h) || Objects.isNull(i)){
            //Not my proudest if
            System.out.println("Settings Invalid! Making new file with defaults!");
            CreateSettingsFile();
        }
    }

    public static boolean CreateSettingsFile() {
        try{
            Files.deleteIfExists(SettingsPath);
            Files.createFile(SettingsPath);
        } catch (IOException e){
            System.out.println("Failed to create settings file!: "+e.getMessage());
            return false;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException e){
            System.out.println("Could not Create Parser Instance! Error: "+e.getMessage());
            return false;
        }

        if(Objects.isNull(builder)){
            System.out.println("Null builder");
            return false;
        }

        // Create a new Document
        Document document = builder.newDocument();
        Element rootSettings = document.createElement("settings");
        rootSettings.setAttribute("autostart", "false");
        rootSettings.setAttribute("injectonstartup", "false");
        rootSettings.setAttribute("classfilecollection", "true");
        rootSettings.setAttribute("classfileloadmessages", "true");
        rootSettings.setAttribute("consolewindow", "true");
        rootSettings.setAttribute("funclooptimeout", "10");
        rootSettings.setAttribute("jnienvtimeout", "100");
        rootSettings.setAttribute("dymanicclassfilecollection", "false");
        rootSettings.setAttribute("dymanicclassfilecp", "None");
        document.appendChild(rootSettings);

        // Create a Transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (javax.xml.transform.TransformerConfigurationException e){
            System.out.println("Error with transformer factory: "+e.getMessage());
            return false;
        }

        if(Objects.isNull(transformer)){
            System.out.println("Null transformer");
            return false;
        }

        File xmlFile = SettingsPath.toFile();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(xmlFile));
        } catch (TransformerException e) {
            System.out.println("Error transforming: "+e.getMessage());
            return false;
        }

        System.out.println("Created settings file with default values");
        return true;

    }

    public static StartupSettings CheckAndLoadFile() {
        if(!CheckSettingsFile()){
            if(!CreateSettingsFile()){
                System.out.println("Failed to create settings file!");
            }
        }
        VerifySettings();

        //Load File
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException e){
            System.out.println("Could not Create Parser Instance! Error: "+e.getMessage());
            return null;
        }

        if(Objects.isNull(builder)){
            System.out.println("Null builder");
            return null;
        }

        //Parse data
        Document doc = null;
        try {
            doc = builder.parse(SettingsPath.toString());
        } catch (IOException | SAXException e){
            System.out.println("Document was null: "+e.getMessage());
            return null;
        }

        // Step 3: Read Data
        // For example, let's print out all the <title> elements in the XML
        NodeList mainNodes = doc.getElementsByTagName("settings");

        File xmlFile = SettingsPath.toFile();
        StartupSettings settings = new StartupSettings();

        //Check null of first element
        Node settingsnode = mainNodes.item(0);
        if(Objects.isNull(settingsnode)){
            System.out.println("Settings node is empty!");
            return null;
        }


        NamedNodeMap attributes = settingsnode.getAttributes();
        Node a = attributes.getNamedItem("autostart");
        Node b = attributes.getNamedItem("injectonstartup");
        Node c = attributes.getNamedItem("classfilecollection");
        Node d = attributes.getNamedItem("classfileloadmessages");
        Node e = attributes.getNamedItem("consolewindow");
        Node f = attributes.getNamedItem("funclooptimeout");
        Node g = attributes.getNamedItem("jnienvtimeout");
        Node h = attributes.getNamedItem("dymanicclassfilecollection");
        Node i = attributes.getNamedItem("dymanicclassfilecp");

        settings.IsAutoStart = Boolean.parseBoolean(a.getNodeValue());
        settings.IsInjectOnStartup = Boolean.parseBoolean(b.getNodeValue());
        settings.IsClassFileCollection = Boolean.parseBoolean(c.getNodeValue());
        settings.IsClassFileLoadMessages = Boolean.parseBoolean(d.getNodeValue());
        settings.IsConsoleWindow = Boolean.parseBoolean(e.getNodeValue());
        settings.FuncLoopTimeout = Integer.parseInt(f.getNodeValue());
        settings.JNIEnvTimeout = Integer.parseInt(g.getNodeValue());
        settings.IsDynamicClassFileCollection = Boolean.parseBoolean(h.getNodeValue());
        settings.DynamicClassFileCollectionPath = i.getNodeValue();
        return settings;
    }

    public static void WriteSettingsFile(StartupSettings settings) {
        if(!CheckSettingsFile()){
            if(!CreateSettingsFile()){
                System.out.println("Startup Settings file does not exist and failed to create!");
            }
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException e){
            System.out.println("Could not Create Parser Instance! Error: "+e.getMessage());
            return;
        }

        if(Objects.isNull(builder)){
            System.out.println("Null builder");
            return;
        }

        // Create a new Document
        Document document = builder.newDocument();
        Element rootSettings = document.createElement("settings");
        rootSettings.setAttribute("autostart", Boolean.toString(settings.IsAutoStart));
        rootSettings.setAttribute("injectonstartup", Boolean.toString(settings.IsInjectOnStartup));
        rootSettings.setAttribute("classfilecollection", Boolean.toString(settings.IsClassFileCollection));
        rootSettings.setAttribute("classfileloadmessages", Boolean.toString(settings.IsClassFileLoadMessages));
        rootSettings.setAttribute("consolewindow", Boolean.toString(settings.IsConsoleWindow));
        rootSettings.setAttribute("funclooptimeout", Integer.toString(settings.FuncLoopTimeout));
        rootSettings.setAttribute("jnienvtimeout", Integer.toString(settings.JNIEnvTimeout));
        rootSettings.setAttribute("dymanicclassfilecollection", Boolean.toString(settings.IsDynamicClassFileCollection));
        rootSettings.setAttribute("dymanicclassfilecp", settings.DynamicClassFileCollectionPath);
        document.appendChild(rootSettings);

        // Create a Transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (javax.xml.transform.TransformerConfigurationException e){
            System.out.println("Error with transformer factory: "+e.getMessage());
            return;
        }

        if(Objects.isNull(transformer)){
            System.out.println("Null transformer");
            return;
        }

        File xmlFile = SettingsPath.toFile();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(xmlFile));
        } catch (TransformerException e) {
            System.out.println("Error transforming: "+e.getMessage());
            return;
        }

        System.out.println("Wrote new settings values!");
    }
}
