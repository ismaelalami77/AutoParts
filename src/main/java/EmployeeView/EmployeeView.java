package EmployeeView;

import Login.Login;
import Login.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmployeeView {

    private Stage stage;
    private BorderPane root;

    private User user;

    private EmployeeProductsView employeeProductsView;
    private CashierScene cashierScene;
    private EmployeeReceiptsView employeeReceiptsView;

    public EmployeeView(User user) {
        this.user = user;

        root = new BorderPane();
        stage = new Stage();

        employeeProductsView = new EmployeeProductsView();
        cashierScene = new CashierScene(user);
        employeeReceiptsView = new EmployeeReceiptsView(user);

        VBox sidebar = createSidebar();

        root.setLeft(sidebar);
        showHome();

        Scene scene = new Scene(root, 1000, 650);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setTitle("AutoParts Employee - " + user.getFullName());
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.getStyleClass().add("app-sidebar");

        Button homeBtn = createMenuButton("Home");
        Button productsBtn = createMenuButton("Products");
        Button cashierBtn = createMenuButton("Cashier");
        Button receiptsBtn = createMenuButton("Receipts");
        Button logoutBtn = createMenuButton("Logout");

        homeBtn.setOnAction(e -> showHome());

        productsBtn.setOnAction(e -> {
            employeeProductsView.loadProducts();
            root.setCenter(employeeProductsView.getRoot());
        });

        cashierBtn.setOnAction(e -> {
            cashierScene.refreshProducts();
            root.setCenter(cashierScene.getRoot());
        });

        receiptsBtn.setOnAction(e -> {
            employeeReceiptsView.loadReceipts();
            root.setCenter(employeeReceiptsView.getRoot());
        });

        logoutBtn.setOnAction(e -> {
            Login login = new Login();
            login.showStage();
            stage.close();
        });

        VBox topButtons = new VBox(15);
        topButtons.setAlignment(Pos.CENTER);
        topButtons.getChildren().addAll(
                homeBtn,
                productsBtn,
                cashierBtn,
                receiptsBtn
        );

        VBox bottomButtons = new VBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.getChildren().add(logoutBtn);

        sidebar.getChildren().addAll(topButtons, bottomButtons);
        VBox.setVgrow(bottomButtons, Priority.ALWAYS);

        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setPrefHeight(40);
        button.getStyleClass().add("sidebar-button");

        return button;
    }

    private void showHome() {
        Label homePage = new Label("Welcome Employee: " + user.getFullName());
        homePage.getStyleClass().add("home-title");
        root.setCenter(homePage);
    }

    public void showStage() {
        stage.show();
    }
}
