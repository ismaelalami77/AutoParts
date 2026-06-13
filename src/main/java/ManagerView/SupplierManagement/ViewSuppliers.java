package ManagerView.SupplierManagement;

import Connection.SupplierDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
public class ViewSuppliers {

    public static ArrayList<Supplier> suppliers = new ArrayList<>();
    public static ObservableList<Supplier> observableSuppliers = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Supplier> suppliersTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageSupplierText;
    private TextField searchTextField;

    private Button addBtn;
    private Button updateBtn;

    private UpdateSupplierScene updateSupplierScene;
    private AddSupplierScene addSupplierScene;

    public ViewSuppliers() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadSuppliers();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageSupplierText = new Text("Manage Suppliers");
        manageSupplierText.getStyleClass().add("page-title");

        suppliersTable = new TableView<>();
        suppliersTable.getStyleClass().add("employees-table");

        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        TableColumn<Supplier, String> supplierNameCol = new TableColumn<>("Supplier Name");
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        suppliersTable.getColumns().addAll(
                idCol,
                supplierNameCol,
                phoneCol,
                emailCol,
                addressCol
        );

        suppliersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        suppliersTable.setPrefHeight(420);
        suppliersTable.setItems(observableSuppliers);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search supplier by name, phone, email, or address...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(suppliersTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageSupplierText,
                suppliersTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Supplier");
        updateBtn = new Button("Update Supplier");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addSupplierScene = new AddSupplierScene(this);
            addSupplierScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadSuppliers() {
        suppliers.clear();
        observableSuppliers.clear();

        suppliers.addAll(SupplierDAO.getAllSuppliers());
        observableSuppliers.addAll(suppliers);
    }

    private void updateAction() {
        Supplier selectedSupplier = suppliersTable.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a supplier to update");
            return;
        }

        updateSupplierScene = new UpdateSupplierScene(this, selectedSupplier);
        updateSupplierScene.showStage();
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableSuppliers.setAll(suppliers);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Supplier> filtered = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            String supplierName = supplier.getSupplierName() == null ? "" : supplier.getSupplierName().toLowerCase();
            String phone = supplier.getPhone() == null ? "" : supplier.getPhone().toLowerCase();
            String email = supplier.getEmail() == null ? "" : supplier.getEmail().toLowerCase();
            String address = supplier.getAddress() == null ? "" : supplier.getAddress().toLowerCase();

            if (supplierName.contains(query)
                    || phone.contains(query)
                    || email.contains(query)
                    || address.contains(query)) {
                filtered.add(supplier);
            }
        }

        observableSuppliers.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
