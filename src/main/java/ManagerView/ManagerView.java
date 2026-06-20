package ManagerView;

import Login.Login;
import Login.User;
import ManagerView.BranchManagement.ViewBranches;
import ManagerView.CategoryManagement.ViewCategories;
import ManagerView.CustomerManagement.ViewCustomers;
import ManagerView.EmployeeManagement.ViewEmployees;
import ManagerView.InventoryManagement.ViewBranchInventory;
import ManagerView.InventoryManagement.ViewWarehouseInventory;
import ManagerView.ProductManagement.ViewProducts;
import ManagerView.PurchaseManagement.ViewPurchaseOrders;
import ManagerView.ReportsManagement.ViewReports;
import ManagerView.SupplierManagement.ViewSuppliers;
import ManagerView.TransferManagement.ViewTransfers;
import ManagerView.WarehouseManagement.ViewWarehouses;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManagerView {
    private Stage stage;
    private BorderPane root;
    private User managerUser;


    private ViewEmployees viewEmployees = new ViewEmployees();
    private ViewSuppliers viewSuppliers = new ViewSuppliers();
    private ViewProducts viewProducts = new ViewProducts();
    private ViewCategories viewCategories = new ViewCategories();
    private ViewBranches viewBranches = new ViewBranches();
    private ViewWarehouses viewWarehouses = new ViewWarehouses();
    private ManagerDashboardView dashboardView = new ManagerDashboardView();
    private ViewBranchInventory viewBranchInventory = new ViewBranchInventory();
    private ViewWarehouseInventory viewWarehouseInventory = new ViewWarehouseInventory();
    private ViewPurchaseOrders viewPurchaseOrders = new ViewPurchaseOrders();
    private ViewTransfers viewTransfers = new ViewTransfers();
    private ViewCustomers viewCustomers = new ViewCustomers();
    private ViewReports viewReports = new ViewReports();

    public ManagerView(User user) {
        this.managerUser = user;
        root = new BorderPane();
        root.getStyleClass().add("app-root");
        stage = new Stage();

        VBox sidebar = createSidebar();

        root.setLeft(sidebar);
        showDashboard();

        Scene scene = new Scene(root, 1280, 760);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        String userFullName = user.getFullName();
        stage.setTitle("AutoParts - " + userFullName);
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(760);
        stage.setMaximized(true);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(16, 18, 16, 18));
        sidebar.setPrefWidth(240);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.getStyleClass().add("app-sidebar");

        Button sidebarToggleButton = createSidebarToggleButton();
        sidebarToggleButton.setOnAction(e -> root.setLeft(createCollapsedSidebar()));

        Label brandLabel = new Label("AutoParts");
        brandLabel.getStyleClass().add("sidebar-brand");

        Label userLabel = new Label(managerUser.getFullName());
        userLabel.getStyleClass().add("sidebar-user");

        VBox titleBox = new VBox(3, brandLabel, userLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        HBox header = new HBox(10, sidebarToggleButton, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("sidebar-header");

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

        Button branchInventoryBtn = createMenuButton("Branch Stock");
        branchInventoryBtn.setOnAction(e -> root.setCenter(viewBranchInventory.getRoot()));

        Button warehouseInventoryBtn = createMenuButton("Warehouse Stock");
        warehouseInventoryBtn.setOnAction(e -> root.setCenter(viewWarehouseInventory.getRoot()));

        Button purchasesBtn = createMenuButton("Purchases");
        purchasesBtn.setOnAction(e -> root.setCenter(viewPurchaseOrders.getRoot()));

        Button transfersBtn = createMenuButton("Transfers");
        transfersBtn.setOnAction(e -> root.setCenter(viewTransfers.getRoot()));

        Button customersBtn = createMenuButton("Customers");
        customersBtn.setOnAction(e -> root.setCenter(viewCustomers.getRoot()));

        Button reportsBtn = createMenuButton("Reports");
        reportsBtn.setOnAction(e -> root.setCenter(viewReports.getRoot()));

        Button logoutButton = createMenuButton("Logout");



        logoutButton.setOnAction(e ->{
            Login login = new Login();
            login.showStage();
            stage.close();
        });

        VBox topButtons = new VBox(4);
        topButtons.setAlignment(Pos.TOP_CENTER);
        topButtons.setFillWidth(true);
        topButtons.getChildren().addAll(
                createSectionLabel("Overview"),
                homeBtn,
                dashboardButton,
                createSectionLabel("People"),
                employeesBtn,
                customersBtn,
                createSectionLabel("Locations"),
                branchesBtn,
                warehousesBtn,
                createSectionLabel("Catalog"),
                suppliersBtn,
                categoriesBtn,
                productsBtn,
                createSectionLabel("Inventory"),
                branchInventoryBtn,
                warehouseInventoryBtn,
                purchasesBtn,
                transfersBtn,
                createSectionLabel("Analysis"),
                reportsBtn
        );

        VBox bottomButtons = new VBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.getChildren().add(logoutButton);

        sidebar.getChildren().addAll(header, topButtons, bottomButtons);

        VBox.setVgrow(topButtons, Priority.ALWAYS);

        return sidebar;
    }

    private VBox createCollapsedSidebar() {
        VBox collapsedSidebar = new VBox(12);
        collapsedSidebar.setAlignment(Pos.TOP_CENTER);
        collapsedSidebar.setPadding(new Insets(22, 8, 22, 8));
        collapsedSidebar.getStyleClass().add("app-sidebar-collapsed");

        Button showButton = createSidebarToggleButton();
        showButton.setOnAction(e -> root.setLeft(createSidebar()));

        collapsedSidebar.getChildren().add(showButton);
        return collapsedSidebar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(196);
        button.setMinHeight(31);
        button.getStyleClass().add("sidebar-button");

        return button;
    }

    private Button createSidebarToggleButton() {
        Button button = new Button("\u2630");
        button.setMinSize(38, 38);
        button.setPrefSize(38, 38);
        button.setMaxSize(38, 38);
        button.getStyleClass().add("sidebar-toggle-button");
        return button;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("sidebar-section");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public void showStage() {
        stage.setMaximized(true);
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
