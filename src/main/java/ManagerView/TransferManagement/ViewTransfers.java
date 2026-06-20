package ManagerView.TransferManagement;

import Connection.BranchDAO;
import Connection.ProductDAO;
import Connection.TransferDAO;
import Connection.WarehouseDAO;
import ManagerView.BranchManagement.Branch;
import ManagerView.ProductManagement.Product;
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

public class ViewTransfers {
    private final BorderPane root;
    private final ObservableList<TransferRecord> transfers = FXCollections.observableArrayList();
    private final ComboBox<Warehouse> warehouseComboBox;
    private final ComboBox<Branch> branchComboBox;
    private final ComboBox<Product> productComboBox;
    private final TextField quantityField;
    private final Label stockHintLabel;

    public ViewTransfers() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        TableView<TransferRecord> table = createTable();
        VBox center = new VBox(15, UIHelperC.createTitleText("Warehouse Transfers"), table);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(25));
        center.getStyleClass().add("employee-content");

        warehouseComboBox = new ComboBox<>();
        warehouseComboBox.setPromptText("From Warehouse");
        warehouseComboBox.getItems().addAll(WarehouseDAO.getAllWarehouses());
        warehouseComboBox.setPrefSize(250, 44);

        branchComboBox = new ComboBox<>();
        branchComboBox.setPromptText("To Branch");
        branchComboBox.getItems().addAll(BranchDAO.getAllBranches());
        branchComboBox.setPrefSize(250, 44);

        productComboBox = new ComboBox<>();
        productComboBox.setPromptText("Choose Product");
        productComboBox.setPrefSize(250, 44);

        quantityField = UIHelperC.createStyledTextField("Quantity");
        stockHintLabel = new Label("Choose a warehouse to see stock.");
        stockHintLabel.getStyleClass().add("form-hint");

        warehouseComboBox.setOnAction(e -> loadWarehouseProducts());
        productComboBox.setOnAction(e -> updateStockHint());

        Button saveButton = UIHelperC.createStyledButton("Transfer Stock");
        saveButton.setOnAction(e -> saveTransfer());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.add(UIHelperC.createInfoText("Warehouse:"), 0, 0);
        form.add(warehouseComboBox, 1, 0);
        form.add(UIHelperC.createInfoText("Branch:"), 0, 1);
        form.add(branchComboBox, 1, 1);
        form.add(UIHelperC.createInfoText("Product:"), 0, 2);
        form.add(productComboBox, 1, 2);
        form.add(stockHintLabel, 1, 3);
        form.add(UIHelperC.createInfoText("Quantity:"), 0, 4);
        form.add(quantityField, 1, 4);

        VBox left = new VBox(18, UIHelperC.createInfoText("New Transfer"), form, saveButton);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(25));
        left.getStyleClass().add("employee-side-actions");

        root.setLeft(left);
        root.setCenter(center);

        loadTransfers();
    }

    private TableView<TransferRecord> createTable() {
        TableView<TransferRecord> table = new TableView<>();
        table.getStyleClass().add("employees-table");

        TableColumn<TransferRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transferId"));

        TableColumn<TransferRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transferDate"));

        TableColumn<TransferRecord, String> warehouseCol = new TableColumn<>("Warehouse");
        warehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

        TableColumn<TransferRecord, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<TransferRecord, Integer> itemCol = new TableColumn<>("Items");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemCount"));

        TableColumn<TransferRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, dateCol, warehouseCol, branchCol, itemCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(520);
        table.setItems(transfers);
        return table;
    }

    private void loadTransfers() {
        transfers.setAll(TransferDAO.getAllTransfers());
    }

    private void saveTransfer() {
        Warehouse warehouse = warehouseComboBox.getValue();
        Branch branch = branchComboBox.getValue();
        Product product = productComboBox.getValue();
        if (warehouse == null || branch == null || product == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose warehouse, branch, and product.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity must be a whole number.");
            return;
        }

        if (quantity <= 0) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity must be positive.");
            return;
        }

        if (TransferDAO.createTransfer(warehouse.getWarehouseId(), branch.getBranchId(), product.getProductId(), quantity)) {
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Transfer saved and stock moved.");
            quantityField.clear();
            loadWarehouseProducts();
            loadTransfers();
        } else {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Not enough stock in this warehouse.");
        }
    }

    public BorderPane getRoot() {
        refreshFormData();
        loadTransfers();
        return root;
    }

    private void refreshFormData() {
        Warehouse selectedWarehouse = warehouseComboBox.getValue();
        Branch selectedBranch = branchComboBox.getValue();
        Product selectedProduct = productComboBox.getValue();
        warehouseComboBox.getItems().setAll(WarehouseDAO.getAllWarehouses());
        branchComboBox.getItems().setAll(BranchDAO.getAllBranches());
        if (selectedWarehouse != null) {
            warehouseComboBox.getItems().stream()
                    .filter(warehouse -> warehouse.getWarehouseId() == selectedWarehouse.getWarehouseId())
                    .findFirst()
                    .ifPresent(warehouseComboBox::setValue);
        }
        if (selectedBranch != null) {
            branchComboBox.getItems().stream()
                    .filter(branch -> branch.getBranchId() == selectedBranch.getBranchId())
                    .findFirst()
                    .ifPresent(branchComboBox::setValue);
        }
        loadWarehouseProducts();
        if (selectedProduct != null) {
            productComboBox.getItems().stream()
                    .filter(product -> product.getProductId() == selectedProduct.getProductId())
                    .findFirst()
                    .ifPresent(productComboBox::setValue);
        }
    }

    private void loadWarehouseProducts() {
        Warehouse warehouse = warehouseComboBox.getValue();
        productComboBox.getItems().clear();
        productComboBox.setValue(null);

        if (warehouse == null) {
            stockHintLabel.setText("Choose a warehouse to see stock.");
            return;
        }

        productComboBox.getItems().setAll(ProductDAO.getProductsForWarehouse(warehouse.getWarehouseId()));
        if (productComboBox.getItems().isEmpty()) {
            stockHintLabel.setText("No stock available in this warehouse.");
        } else {
            stockHintLabel.setText(productComboBox.getItems().size() + " products available to transfer.");
        }
    }

    private void updateStockHint() {
        Product product = productComboBox.getValue();
        if (product != null) {
            stockHintLabel.setText("Available in warehouse: " + product.getQuantity());
        }
    }
}
