package EmployeeView;

import Login.Login;
import Login.User;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
        root.getStyleClass().add("app-root");
        stage = new Stage();

        VBox sidebar = createSidebar();

        root.setLeft(sidebar);
        showHome();

        Scene scene = new Scene(root, 1150, 700);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setTitle("AutoParts Employee - " + user.getFullName());
        stage.setScene(scene);
        stage.setMinWidth(1150);
        stage.setMinHeight(700);
        stage.setMaximized(true);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(22));
        sidebar.setPrefWidth(240);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.getStyleClass().add("app-sidebar");

        Button sidebarToggleButton = createSidebarToggleButton();
        sidebarToggleButton.setOnAction(e -> root.setLeft(createCollapsedSidebar()));

        Label brandLabel = new Label("AutoParts");
        brandLabel.getStyleClass().add("sidebar-brand");

        Label userLabel = new Label(user.getFullName());
        userLabel.getStyleClass().add("sidebar-user");

        VBox titleBox = new VBox(3, brandLabel, userLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        HBox header = new HBox(10, sidebarToggleButton, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("sidebar-header");

        Button homeBtn = createMenuButton("Home");
        Button productsBtn = createMenuButton("Products");
        Button cashierBtn = createMenuButton("Cashier");
        Button receiptsBtn = createMenuButton("Receipts");
        Button logoutBtn = createMenuButton("Logout");

        homeBtn.setOnAction(e -> showHome());

        productsBtn.setOnAction(e -> {
            if (!ensureEmployeeHasBranch()) {
                return;
            }

            if (employeeProductsView == null) {
                employeeProductsView = new EmployeeProductsView(user);
            }

            employeeProductsView.loadProducts();
            root.setCenter(employeeProductsView.getRoot());
        });

        cashierBtn.setOnAction(e -> {
            if (!ensureEmployeeHasBranch()) {
                return;
            }

            if (cashierScene == null) {
                cashierScene = new CashierScene(user);
            }

            cashierScene.refreshProducts();
            cashierScene.refreshCustomers();
            root.setCenter(cashierScene.getRoot());
        });

        receiptsBtn.setOnAction(e -> {
            if (employeeReceiptsView == null) {
                employeeReceiptsView = new EmployeeReceiptsView(user);
            }

            employeeReceiptsView.loadReceipts();
            root.setCenter(employeeReceiptsView.getRoot());
        });

        logoutBtn.setOnAction(e -> {
            Login login = new Login();
            login.showStage();
            stage.close();
        });

        VBox topButtons = new VBox(10);
        topButtons.setAlignment(Pos.TOP_CENTER);
        topButtons.getChildren().addAll(
                createSectionLabel("Work"),
                homeBtn,
                productsBtn,
                cashierBtn,
                receiptsBtn
        );

        VBox bottomButtons = new VBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.getChildren().add(logoutBtn);

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
        button.setMinHeight(42);
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

    private void showHome() {
        Label homePage = new Label("Welcome Employee: " + user.getFullName());
        homePage.getStyleClass().add("home-title");
        root.setCenter(homePage);
    }

    private boolean ensureEmployeeHasBranch() {
        if (user.getBranchId() > 0) {
            return true;
        }

        UIHelperC.showAlert(
                Alert.AlertType.WARNING,
                "This employee is not assigned to a branch. Please assign a branch first."
        );
        return false;
    }

    public void showStage() {
        stage.setMaximized(true);
        stage.show();
    }
}
