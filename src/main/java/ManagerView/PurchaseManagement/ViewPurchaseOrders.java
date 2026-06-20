package ManagerView.PurchaseManagement;

import Connection.ProductDAO;
import Connection.PurchaseOrderDAO;
import Connection.SupplierDAO;
import Connection.WarehouseDAO;
import ManagerView.ProductManagement.Product;
import ManagerView.SupplierManagement.Supplier;
import ManagerView.WarehouseManagement.Warehouse;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ViewPurchaseOrders {
    private final BorderPane root;
    private final ObservableList<PurchaseOrder> orders = FXCollections.observableArrayList();
    private final ComboBox<Supplier> supplierComboBox;
    private final ComboBox<Warehouse> warehouseComboBox;
    private final ComboBox<Product> productComboBox;
    private final TextField quantityField;
    private final TextField unitCostField;
    private final Label productHintLabel;

    public ViewPurchaseOrders() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        TableView<PurchaseOrder> table = createTable();
        VBox center = new VBox(15, UIHelperC.createTitleText("Purchase Orders"), table);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(25));
        center.getStyleClass().add("employee-content");

        supplierComboBox = new ComboBox<>();
        supplierComboBox.setPromptText("Choose Supplier");
        supplierComboBox.getItems().addAll(SupplierDAO.getAllSuppliers());
        supplierComboBox.setPrefSize(250, 44);

        warehouseComboBox = new ComboBox<>();
        warehouseComboBox.setPromptText("Choose Warehouse");
        warehouseComboBox.getItems().addAll(WarehouseDAO.getAllWarehouses());
        warehouseComboBox.setPrefSize(250, 44);

        productComboBox = new ComboBox<>();
        productComboBox.setPromptText("Choose Product");
        productComboBox.setPrefSize(250, 44);

        quantityField = UIHelperC.createStyledTextField("Quantity");
        unitCostField = UIHelperC.createStyledTextField("Unit Cost");
        productHintLabel = new Label("Choose a supplier to see products.");
        productHintLabel.getStyleClass().add("form-hint");

        supplierComboBox.setOnAction(e -> loadSupplierProducts());
        productComboBox.setOnAction(e -> fillSupplierCost());

        Button saveButton = UIHelperC.createStyledButton("Receive Stock");
        saveButton.setOnAction(e -> saveOrder());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.add(UIHelperC.createInfoText("Supplier:"), 0, 0);
        form.add(supplierComboBox, 1, 0);
        form.add(UIHelperC.createInfoText("Warehouse:"), 0, 1);
        form.add(warehouseComboBox, 1, 1);
        form.add(UIHelperC.createInfoText("Product:"), 0, 2);
        form.add(productComboBox, 1, 2);
        form.add(productHintLabel, 1, 3);
        form.add(UIHelperC.createInfoText("Quantity:"), 0, 4);
        form.add(quantityField, 1, 4);
        form.add(UIHelperC.createInfoText("Unit Cost:"), 0, 5);
        form.add(unitCostField, 1, 5);

        VBox left = new VBox(18, UIHelperC.createInfoText("New Purchase"), form, saveButton);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(25));
        left.getStyleClass().add("employee-side-actions");

        root.setLeft(left);
        root.setCenter(center);

        loadOrders();
    }

    private TableView<PurchaseOrder> createTable() {
        TableView<PurchaseOrder> table = new TableView<>();
        table.getStyleClass().add("employees-table");

        TableColumn<PurchaseOrder, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("purchaseOrderId"));

        TableColumn<PurchaseOrder, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<PurchaseOrder, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<PurchaseOrder, String> warehouseCol = new TableColumn<>("Warehouse");
        warehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

        TableColumn<PurchaseOrder, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<PurchaseOrder, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, dateCol, supplierCol, warehouseCol, totalCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(520);
        table.setItems(orders);
        return table;
    }

    private void loadOrders() {
        orders.setAll(PurchaseOrderDAO.getAllPurchaseOrders());
    }

    private void saveOrder() {
        Supplier supplier = supplierComboBox.getValue();
        Warehouse warehouse = warehouseComboBox.getValue();
        Product product = productComboBox.getValue();
        if (supplier == null || warehouse == null || product == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose supplier, warehouse, and product.");
            return;
        }

        int quantity;
        double unitCost;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            unitCost = Double.parseDouble(unitCostField.getText().trim());
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity and unit cost must be valid numbers.");
            return;
        }

        if (quantity <= 0 || unitCost < 0) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity must be positive and cost cannot be negative.");
            return;
        }

        if (PurchaseOrderDAO.createPurchaseOrder(
                supplier.getSupplierId(), warehouse.getWarehouseId(), product.getProductId(), quantity, unitCost)) {
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Purchase saved and warehouse stock updated.");
            quantityField.clear();
            unitCostField.clear();
            loadSupplierProducts();
            loadOrders();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Purchase could not be saved.");
        }
    }

    public BorderPane getRoot() {
        refreshFormData();
        loadOrders();
        return root;
    }

    private void refreshFormData() {
        Supplier selectedSupplier = supplierComboBox.getValue();
        Warehouse selectedWarehouse = warehouseComboBox.getValue();
        Product selectedProduct = productComboBox.getValue();
        supplierComboBox.getItems().setAll(SupplierDAO.getAllSuppliers());
        warehouseComboBox.getItems().setAll(WarehouseDAO.getAllWarehouses());
        if (selectedSupplier != null) {
            supplierComboBox.getItems().stream()
                    .filter(supplier -> supplier.getSupplierId() == selectedSupplier.getSupplierId())
                    .findFirst()
                    .ifPresent(supplierComboBox::setValue);
        }
        if (selectedWarehouse != null) {
            warehouseComboBox.getItems().stream()
                    .filter(warehouse -> warehouse.getWarehouseId() == selectedWarehouse.getWarehouseId())
                    .findFirst()
                    .ifPresent(warehouseComboBox::setValue);
        }
        loadSupplierProducts();
        if (selectedProduct != null) {
            productComboBox.getItems().stream()
                    .filter(product -> product.getProductId() == selectedProduct.getProductId())
                    .findFirst()
                    .ifPresent(productComboBox::setValue);
        }
    }

    private void loadSupplierProducts() {
        Supplier supplier = supplierComboBox.getValue();
        productComboBox.getItems().clear();
        productComboBox.setValue(null);
        unitCostField.clear();

        if (supplier == null) {
            productHintLabel.setText("Choose a supplier to see products.");
            return;
        }

        productComboBox.getItems().setAll(ProductDAO.getProductsForSupplier(supplier.getSupplierId()));
        if (productComboBox.getItems().isEmpty()) {
            productHintLabel.setText("No products linked to this supplier.");
        } else {
            productHintLabel.setText(productComboBox.getItems().size() + " supplier products available.");
        }
    }

    private void fillSupplierCost() {
        Product product = productComboBox.getValue();
        if (product != null) {
            unitCostField.setText(String.valueOf(product.getCostPrice()));
        }
    }
}
