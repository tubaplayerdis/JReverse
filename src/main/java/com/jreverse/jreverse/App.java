package com.jreverse.jreverse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class App extends Application {

    private static Scene thescene;
    public static Stage thestage;
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader =  new FXMLLoader();
        return loader.load(App.class.getResource(fxml + ".fxml"));
    }


    @Override
    public void start(Stage stage) throws IOException {
        thescene = new Scene(loadFXML("startup"), 600, 400);
        stage.setResizable(false);
        stage.setTitle("JReverse");
        stage.setScene(thescene);
        stage.show();
        thestage = stage;
    }

    public static void main(String[] args) {
        launch();
    }
    }

