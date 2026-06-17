package ManagerView;

import Login.Login;
import Login.User;
import ManagerView.BranchManagement.ViewBranches;
import ManagerView.CategoryManagement.ViewCategories;
import ManagerView.EmployeeManagement.ViewEmployees;
import ManagerView.ProductManagement.ViewProducts;
import ManagerView.SupplierManagement.ViewSuppliers;
import ManagerView.WarehouseManagement.ViewWarehouses;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManagerView {
    private Stage stage;
    private BorderPane root;


    private ViewEmployees viewEmployees = new ViewEmployees();
    private ViewSuppliers viewSuppliers = new ViewSuppliers();
    private ViewProducts viewProducts = new ViewProducts();
    private ViewCategories viewCategories = new ViewCategories();
    private ViewBranches viewBranches = new ViewBranches();
    private ViewWarehouses viewWarehouses = new ViewWarehouses();
    private ManagerDashboardView dashboardView = new ManagerDashboardView();

    public ManagerView(User user) {
        root = new BorderPane();
        stage = new Stage();

        VBox sidebar = createSidebar();

        root.setLeft(sidebar);
        showDashboard();

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
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
        sidebar.getStyleClass().add("app-sidebar");

        Button homeBtn = createMenuButton("Home");
        homeBtn.setOnAction(e -> showDashboard());

        Button employeesBtn = createMenuButton("Employees");
        employeesBtn.setOnAction(e -> root.setCenter(viewEmployees.getRoot()));

        Button suppliersBtn = createMenuButton("Suppliers");
        suppliersBtn.setOnAction(e -> root.setCenter(viewSuppliers.getRoot()));

        Button productsBtn = createMenuButton("Products");
        productsBtn.setOnAction(e -> root.setCenter(viewProducts.getRoot()));

        Button categoriesBtn = createMenuButton("Categories");
        categoriesBtn.setOnAction(e -> root.setCenter(viewCategories.getRoot()));

        Button branchesBtn = createMenuButton("Branches");
        branchesBtn.setOnAction(e -> root.setCenter(viewBranches.getRoot()));

        Button dashboardButton = createMenuButton("Dashboard");
        dashboardButton.setOnAction(e -> showDashboard());

        Button warehousesBtn = createMenuButton("Warehouses");
        warehousesBtn.setOnAction(e -> root.setCenter(viewWarehouses.getRoot()));

        Button logoutButton = createMenuButton("Logout");



        logoutButton.setOnAction(e ->{
            Login login = new Login();
            login.showStage();
            stage.close();
        });

        VBox topButtons = new VBox(15);
        topButtons.setAlignment(Pos.CENTER);
        topButtons.getChildren().addAll(
                homeBtn,
                employeesBtn,
                branchesBtn,
                warehousesBtn,
                suppliersBtn,
                categoriesBtn,
                productsBtn,
                dashboardButton
        );

        VBox bottomButtons = new VBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.getChildren().add(logoutButton);

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

    public void showStage() {
        stage.show();
    }

    private void showPage(String pageName) {
        Label page = new Label(pageName);
        page.getStyleClass().add("home-title");
        root.setCenter(page);
    }

    private void showDashboard() {
        dashboardView.loadDashboard();
        root.setCenter(dashboardView.getRoot());
    }
}
