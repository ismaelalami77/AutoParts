package ManagerView.InventoryManagement;

import Connection.InventoryDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ViewWarehouseInventory {
    private final BorderPane root;
    private final ObservableList<InventoryItem> inventory = FXCollections.observableArrayList();
    private final TextField searchField;

    public ViewWarehouseInventory() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        TableView<InventoryItem> table = createTable();
        searchField = UIHelperC.createStyledTextField("Search warehouse stock...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        VBox center = new VBox(15, UIHelperC.createTitleText("Warehouse Inventory"), table, searchField);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(25));
        center.getStyleClass().add("employee-content");

        root.setCenter(center);

        loadInventory();
    }

    private TableView<InventoryItem> createTable() {
        TableView<InventoryItem> table = new TableView<>();
        table.getStyleClass().add("employees-table");

        TableColumn<InventoryItem, String> warehouseCol = new TableColumn<>("Warehouse");
        warehouseCol.setCellValueFactory(new PropertyValueFactory<>("placeName"));

        TableColumn<InventoryItem, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<InventoryItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<InventoryItem, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<InventoryItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<InventoryItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("stockStatus"));

        table.getColumns().addAll(warehouseCol, productCol, categoryCol, brandCol, qtyCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(460);
        table.setItems(inventory);
        return table;
    }

    private void loadInventory() {
        inventory.setAll(InventoryDAO.getWarehouseInventory());
    }

    private void filterTable(String text) {
        ArrayList<InventoryItem> allItems = InventoryDAO.getWarehouseInventory();
        if (text == null || text.trim().isEmpty()) {
            inventory.setAll(allItems);
            return;
        }

        String query = text.trim().toLowerCase();
        inventory.setAll(allItems.stream()
                .filter(item -> contains(item.getPlaceName(), query)
                        || contains(item.getProductName(), query)
                        || contains(item.getCategoryName(), query)
                        || contains(item.getBrand(), query)
                        || contains(item.getStockStatus(), query))
                .toList());
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    public BorderPane getRoot() {
        loadInventory();
        return root;
    }
}
