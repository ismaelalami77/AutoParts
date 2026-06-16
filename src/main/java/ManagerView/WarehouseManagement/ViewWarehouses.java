package ManagerView.WarehouseManagement;

import Connection.WarehouseDAO;
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

public class ViewWarehouses {

    public static ArrayList<Warehouse> warehouses = new ArrayList<>();
    public static ObservableList<Warehouse> observableWarehouses = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Warehouse> warehousesTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageWarehousesText;
    private TextField searchTextField;

    private Button addBtn;
    private Button updateBtn;

    private AddWarehouseScene addWarehouseScene;
    private UpdateWarehouseScene updateWarehouseScene;

    public ViewWarehouses() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadWarehouses();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageWarehousesText = new Text("Manage Warehouses");
        manageWarehousesText.getStyleClass().add("page-title");

        warehousesTable = new TableView<>();
        warehousesTable.getStyleClass().add("employees-table");

        TableColumn<Warehouse, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));

        TableColumn<Warehouse, String> warehouseNameCol = new TableColumn<>("Warehouse Name");
        warehouseNameCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

        TableColumn<Warehouse, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Warehouse, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        warehousesTable.getColumns().addAll(
                idCol,
                warehouseNameCol,
                locationCol,
                phoneCol
        );

        warehousesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        warehousesTable.setPrefHeight(420);
        warehousesTable.setItems(observableWarehouses);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search warehouse by name, location, or phone...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(warehousesTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageWarehousesText,
                warehousesTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Warehouse");
        updateBtn = new Button("Update Warehouse");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addWarehouseScene = new AddWarehouseScene(this);
            addWarehouseScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadWarehouses() {
        warehouses.clear();
        observableWarehouses.clear();

        warehouses.addAll(WarehouseDAO.getAllWarehouses());
        observableWarehouses.addAll(warehouses);
    }

    private void updateAction() {
        Warehouse selectedWarehouse = warehousesTable.getSelectionModel().getSelectedItem();

        if (selectedWarehouse == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a warehouse to update");
            return;
        }

        updateWarehouseScene = new UpdateWarehouseScene(this, selectedWarehouse);
        updateWarehouseScene.showStage();
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableWarehouses.setAll(warehouses);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Warehouse> filtered = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            String warehouseName = warehouse.getWarehouseName() == null ? "" : warehouse.getWarehouseName().toLowerCase();
            String location = warehouse.getLocation() == null ? "" : warehouse.getLocation().toLowerCase();
            String phone = warehouse.getPhone() == null ? "" : warehouse.getPhone().toLowerCase();

            if (warehouseName.contains(query)
                    || location.contains(query)
                    || phone.contains(query)) {
                filtered.add(warehouse);
            }
        }

        observableWarehouses.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
