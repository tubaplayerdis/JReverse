package com.jreverse.jreverse;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    private static Scene thescene;
    public static Stage thestage;

    public static Boolean isOnStartup = false;
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader =  new FXMLLoader();
        return loader.load(App.class.getResource(fxml + ".fxml"));
    }


    @Override
    public void start(Stage stage) throws IOException {
        thescene = new Scene(loadFXML("startup"), 600, 400);
        String susPath = System.getProperty("user.dir");
        File style = new File(susPath+"/stylesheets/style.css");
        thescene.getStylesheets().add(style.toURI().toURL().toExternalForm());
        //App.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        Image image = new Image(susPath+"/icon/JReverseIcon.png");
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.setTitle("JReverse");
        stage.setScene(thescene);
        stage.show();
        thestage = stage;
        JReverseBridge.InitBridge();
    }

    @Override
    public void stop() {
        JReverseBridge.CallCoreFunction("uninjectCore", JReverseBridge.NoneArg);
    }

    public static void main(String[] args) {
        launch();
    }
    }

