package EmployeeView;

import Connection.ProductDAO;
import ManagerView.ProductManagement.Product;
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
import javafx.scene.text.Text;

import java.util.ArrayList;

public class EmployeeProductsView {

    private ArrayList<Product> products = new ArrayList<>();
    private ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Product> productsTable;
    private TextField searchTextField;

    public EmployeeProductsView() {
        root = new BorderPane();

        VBox centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));

        Text title = new Text("Available Products");
        title.getStyleClass().add("page-title");

        productsTable = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<Product, Double> sellingPriceCol = new TableColumn<>("Price");
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        productsTable.getColumns().addAll(
                idCol,
                productNameCol,
                categoryCol,
                supplierCol,
                sellingPriceCol,
                quantityCol,
                descriptionCol
        );

        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productsTable.setPrefHeight(450);
        productsTable.setItems(observableProducts);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search product...");
        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        centerVBox.getChildren().addAll(title, productsTable, searchTextField);
        root.setCenter(centerVBox);

        loadProducts();
    }

    public void loadProducts() {
        products.clear();
        observableProducts.clear();

        products.addAll(ProductDAO.getAllProducts());
        observableProducts.addAll(products);
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableProducts.setAll(products);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Product> filtered = new ArrayList<>();

        for (Product product : products) {
            String productName = product.getProductName() == null ? "" : product.getProductName().toLowerCase();
            String categoryName = product.getCategoryName() == null ? "" : product.getCategoryName().toLowerCase();
            String supplierName = product.getSupplierName() == null ? "" : product.getSupplierName().toLowerCase();
            String description = product.getDescription() == null ? "" : product.getDescription().toLowerCase();

            if (productName.contains(query)
                    || categoryName.contains(query)
                    || supplierName.contains(query)
                    || description.contains(query)) {
                filtered.add(product);
            }
        }

        observableProducts.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
