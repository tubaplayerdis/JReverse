package com.jreverse.jreverse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class StartupSettingsHelper {
    private static final Path SettingsPath = Paths.get(System.getProperty("user.dir")+"/settings.xml");

    public static boolean CheckSettingsFile() {
        return Files.exists(SettingsPath);
    }

    public static boolean CreateSettingsFile() {
        try{
            Files.createFile(SettingsPath);
        } catch (IOException e){
            System.out.println("Failed to create settings file!: "+e.getMessage());
            return false;
        }
        //Create Default Settings
        StartupSettings settings = new StartupSettings();
        settings.IsAutoStart = false;
        settings.IsInjectOnStartup = false;
        settings.IsClassFileCollection = true;
        settings.IsClassFileLoadMessages = true;
        settings.IsConsoleWindow = true;

        //Create JAXBContext Object
        JAXBContext jaxbContext = null;

        // Create JAXB context for your classes
        try{
            jaxbContext = JAXBContext.newInstance(StartupSettings.class);
        } catch (JAXBException e){
            System.out.println("Failed to create JAXBContext: "+e.getMessage());
            return false;
        }

        if(Objects.isNull(jaxbContext)){
            System.out.println("JAXBContext object was null!");
            return false;
        }

        // Create a Marshaller Object
        Marshaller marshaller = null;

        //Create Marshaller and catch
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e){
            System.out.println("Failed to create marshaller: "+e.getMessage());
            return false;
        }

        //Check null state
        if(Objects.isNull(marshaller)){
            System.out.println("Marshaller object was null!");
            return false;
        }

        File xmlFile = SettingsPath.toFile();
        try {
            marshaller.marshal(settings, xmlFile);
        } catch (JAXBException e){
            System.out.println("Failed to marshall: "+e.getMessage());
            return false;
        }

        System.out.println("Marshalled settings file");
        return true;

    }

    public static StartupSettings CheckAndLoadFile() {
        if(!CheckSettingsFile()){
            if(!CreateSettingsFile()){
                System.out.println("Failed to create settings file!");
            }
        }
        //Load File
        //Create JAXBContext Object
        JAXBContext jaxbContext = null;

        // Create JAXB context for your classes
        try{
            jaxbContext = JAXBContext.newInstance(StartupSettings.class);
        } catch (JAXBException e){
            System.out.println("Failed to create JAXBContext: "+e.getMessage());
            return null;
        }

        if(Objects.isNull(jaxbContext)){
            System.out.println("JAXBContext object was null!");
            return null;
        }

        // Create a Marshaller Object
        Unmarshaller unmarshaller = null;

        //Create Marshaller and catch
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e){
            System.out.println("Failed to create marshaller: "+e.getMessage());
            return null;
        }

        //Check null state
        if(Objects.isNull(unmarshaller)){
            System.out.println("Marshaller object was null!");
            return null;
        }

        File xmlFile = SettingsPath.toFile();
        StartupSettings settings = null;
        try {
            settings = (StartupSettings) unmarshaller.unmarshal(xmlFile);
        } catch (JAXBException e){
            System.out.println("Failed to marshall: "+e.getMessage());
            return null;
        }

        if(Objects.isNull(settings)){
            System.out.println("Startup settings null!");
            return null;
        }

        return settings;
    }

    public static void WriteSettingsFile(StartupSettings settings) {
        if(!CheckSettingsFile()){
            if(!CreateSettingsFile()){
                System.out.println("Startup Settings file does not exist and failed to create!");
            }
        }

        //Create JAXBContext Object
        JAXBContext jaxbContext = null;

        // Create JAXB context for your classes
        try{
            jaxbContext = JAXBContext.newInstance(StartupSettings.class);
        } catch (JAXBException e){
            System.out.println("Failed to create JAXBContext: "+e.getMessage());
            return;
        }

        if(Objects.isNull(jaxbContext)){
            System.out.println("JAXBContext object was null!");
            return;
        }

        // Create a Marshaller Object
        Marshaller marshaller = null;

        //Create Marshaller and catch
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e){
            System.out.println("Failed to create marshaller: "+e.getMessage());
            return;
        }

        //Check null state
        if(Objects.isNull(marshaller)){
            System.out.println("Marshaller object was null!");
            return;
        }

        File xmlFile = SettingsPath.toFile();
        try {
            marshaller.marshal(settings, xmlFile);
        } catch (JAXBException e){
            System.out.println("Failed to marshall: "+e.getMessage());
            return;
        }

        System.out.println("Marshalled settings file");
    }
}
