package com.example.autoparts;

import Login.Login;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) {
        Login login = new Login();
        login.showStage();
    }
}
