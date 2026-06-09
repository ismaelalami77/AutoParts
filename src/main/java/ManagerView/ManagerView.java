package ManagerView;

import Login.Login;
import Login.User;
import ManagerView.EmployeeManagement.Employee;
import ManagerView.EmployeeManagement.ViewEmployees;
import com.mysql.cj.log.Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManagerView {
    private Stage stage;
    private Scene scene;
    private BorderPane root;


    private ViewEmployees viewEmployees = new ViewEmployees();

    public ManagerView(User user) {
        root = new BorderPane();
        stage = new Stage();

        VBox sidebar = createSidebar();

        Label homePage = new Label("Home Page");
        homePage.setStyle("-fx-font-size: 30px;");

        root.setLeft(sidebar);
        root.setCenter(homePage);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/AutoParts/style.css").toExternalForm()
        );

        String userFullName = user.getFullName();
        stage.setTitle("AutoParts - " + userFullName);
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setAlignment(Pos.TOP_CENTER);

        sidebar.setStyle("-fx-background-color: #2c3e50;" +
                "-fx-border-color: white;");

        Button homeBtn = createMenuButton("Home");
        Button employeesBtn = createMenuButton("Employees");
        Button productsBtn = createMenuButton("Products");
        Button dashboardButton = createMenuButton("Dashboard");
        Button logoutButton = createMenuButton("Logout");

        homeBtn.setOnAction(e -> showPage("Home Page"));
        employeesBtn.setOnAction(e -> showPage("DP Table Page"));

        employeesBtn.setOnAction(e -> root.setCenter(viewEmployees.getRoot()));
        productsBtn.setOnAction(e -> showPage("Connections Page"));
        dashboardButton.setOnAction(e -> showPage("About Page"));

        logoutButton.setOnAction(e ->{
            Login login = new Login();
            login.showStage();
            stage.close();
        });

        VBox topButtons = new VBox(15);
        topButtons.setAlignment(Pos.CENTER);
        topButtons.getChildren().addAll(homeBtn, employeesBtn, productsBtn, dashboardButton);

        VBox bottomButtons = new VBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.getChildren().add(logoutButton);

        sidebar.getChildren().addAll(topButtons, bottomButtons);

        VBox.setVgrow(bottomButtons, javafx.scene.layout.Priority.ALWAYS);



        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setPrefHeight(40);

        String normalStyle =
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: white;";

        String hoverStyle =
                "-fx-background-color: #34495e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: white;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));

        return button;
    }

    public void showStage() {
        stage.show();
    }

    private void showPage(String pageName) {
        Label page = new Label(pageName);
        page.setStyle("-fx-font-size: 30px;");
        root.setCenter(page);
    }
}
