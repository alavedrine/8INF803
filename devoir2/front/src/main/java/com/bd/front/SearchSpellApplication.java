package com.bd.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchSpellApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SearchSpellApplication.class.getResource("choice-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        stage.setTitle("Spell finder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}