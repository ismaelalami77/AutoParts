package com.example.autoparts;

import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class UIHelperC {

    private static final String fontFamily = "Segoe UI";
    private static final double fieldWidth = 250;
    private static final double fieldHeight = 44;
    private static final String accentColor = "#0f766e";
    private static final String hoverColor = "#0b5f59";


    public static void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    public static Text createTitleText(String content) {
        Text myText = new Text(content);
        myText.setStyle("-fx-font-family: '" + fontFamily + "'; " +
                "-fx-font-weight: bold; " +
                "-fx-fill: #17211f; " +
                "-fx-font-size: 34px;");
        return myText;
    }


    public static Button createStyledButton(String content) {
        Button button = new Button(content);


        button.setPrefSize(fieldWidth, fieldHeight);
        button.setMinSize(fieldWidth, fieldHeight);
        button.setMaxSize(fieldWidth, fieldHeight);

        String style = "-fx-background-color: " + accentColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-family: '" + fontFamily + "';" +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-radius: 10px; " +
                "-fx-border-color: " + accentColor + "; " +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-family: '" + fontFamily + "';" +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-radius: 10px; " +
                "-fx-border-color: " + hoverColor + "; " +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;";

        button.setStyle(style);
        button.setOnMouseEntered(e -> {
            button.setCursor(Cursor.HAND);
            button.setStyle(hoverStyle);
        });
        button.setOnMouseExited(e -> {
            button.setCursor(Cursor.DEFAULT);
            button.setStyle(style);
        });

        return button;
    }


    public static TextField createStyledTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);

        textField.setPrefSize(fieldWidth, fieldHeight);
        textField.setMinSize(fieldWidth, fieldHeight);
        textField.setMaxSize(fieldWidth, fieldHeight);

        textField.setStyle("-fx-background-radius: 10px; " +
                "-fx-background-color: white; " +
                "-fx-font-family: '" + fontFamily + "'; " +
                "-fx-font-size: 15px; " +
                "-fx-text-fill: #1f2d3d; " +
                "-fx-prompt-text-fill: #7b8a97; " +
                "-fx-border-color: #d9e2df; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-insets: 0;");

        return textField;
    }


    public static PasswordField createStyledPassField(String placeholder) {
        PasswordField passField = new PasswordField();
        passField.setPromptText(placeholder);

        passField.setPrefSize(fieldWidth, fieldHeight);
        passField.setMinSize(fieldWidth, fieldHeight);
        passField.setMaxSize(fieldWidth, fieldHeight);

        passField.setStyle("-fx-background-radius: 10px; " +
                "-fx-background-color: white; " +
                "-fx-font-family: '" + fontFamily + "'; " +
                "-fx-font-size: 15px; " +
                "-fx-text-fill: #1f2d3d; " +
                "-fx-prompt-text-fill: #7b8a97; " +
                "-fx-border-color: #d7e1ea; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-insets: 0;");

        return passField;
    }

    public static Text createInfoText(String content) {
        Text infoText = new Text(content);
        infoText.setStyle("-fx-font-family: '" + fontFamily + "'; " +
                "-fx-fill: #2f3f3b; " +
                "-fx-font-weight: 700; " +
                "-fx-font-size: 16px;");
        return infoText;
    }

    public static ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();

        comboBox.setPrefSize(200, 44);
        comboBox.setStyle("-fx-background-radius: 10px; " +
                "-fx-background-color: white; " +
                "-fx-font-family: '" + fontFamily + "';" +
                "-fx-border-color: #d9e2df; " +
                "-fx-border-radius: 10px; " +
                "-fx-font-size: 15px; " +
                "-fx-text-fill: #1f2d3d;");

        return comboBox;
    }

}
