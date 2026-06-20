package EmployeeView;

import Connection.ReceiptDAO;
import Login.User;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class EmployeeReceiptsView {

    private final User user;
    private final BorderPane root;
    private final TableView<ReceiptSale> salesTable;
    private final TableView<ReceiptItem> itemsTable;
    private final ObservableList<ReceiptSale> sales = FXCollections.observableArrayList();
    private final ObservableList<ReceiptItem> items = FXCollections.observableArrayList();

    public EmployeeReceiptsView(User user) {
        this.user = user;

        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));

        Text title = new Text("My Receipts");
        title.getStyleClass().add("page-title");

        salesTable = createSalesTable();
        itemsTable = createItemsTable();

        Button refreshButton = new Button("Refresh");
        Button printButton = new Button("Print Receipt PDF");
        refreshButton.getStyleClass().add("modern-action-button");
        printButton.getStyleClass().add("modern-action-button");

        refreshButton.setOnAction(e -> loadReceipts());
        printButton.setOnAction(e -> printSelectedReceipt());

        HBox actions = new HBox(12, refreshButton, printButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(title, salesTable, itemsTable, actions);
        VBox.setVgrow(salesTable, Priority.ALWAYS);
        VBox.setVgrow(itemsTable, Priority.ALWAYS);

        root.setCenter(content);

        salesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSale, newSale) -> loadItems(newSale));
        loadReceipts();
    }

    private TableView<ReceiptSale> createSalesTable() {
        TableView<ReceiptSale> table = new TableView<>();

        TableColumn<ReceiptSale, Integer> idCol = new TableColumn<>("Receipt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<ReceiptSale, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<ReceiptSale, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));

        TableColumn<ReceiptSale, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<ReceiptSale, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        table.getColumns().addAll(idCol, customerCol, phoneCol, dateCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setItems(sales);

        return table;
    }

    private TableView<ReceiptItem> createItemsTable() {
        TableView<ReceiptItem> table = new TableView<>();

        TableColumn<ReceiptItem, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<ReceiptItem, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ReceiptItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<ReceiptItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        table.getColumns().addAll(productCol, quantityCol, priceCol, subtotalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(240);
        table.setItems(items);

        return table;
    }

    public void loadReceipts() {
        sales.setAll(ReceiptDAO.getSalesByEmployee(user.getId()));
        items.clear();

        if (!sales.isEmpty()) {
            salesTable.getSelectionModel().selectFirst();
        }
    }

    private void loadItems(ReceiptSale sale) {
        if (sale == null) {
            items.clear();
            return;
        }

        items.setAll(ReceiptDAO.getSaleItems(sale.getOrderId()));
    }

    private void printSelectedReceipt() {
        ReceiptSale selectedSale = salesTable.getSelectionModel().getSelectedItem();

        if (selectedSale == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a receipt first!");
            return;
        }

        ArrayList<ReceiptItem> selectedItems = ReceiptDAO.getSaleItems(selectedSale.getOrderId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt PDF");
        fileChooser.setInitialFileName("receipt-" + selectedSale.getOrderId() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ReceiptPdfExporter.export(file, selectedSale, selectedItems, user);
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Receipt PDF saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Receipt PDF could not be saved!");
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
