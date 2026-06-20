package ManagerView.ProductManagement;

import Connection.ProductDAO;
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
public class ViewProducts {

    public static ArrayList<Product> products = new ArrayList<>();
    public static ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Product> productsTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageProductsText;
    private TextField searchTextField;

    private Button addBtn;
    private Button updateBtn;

    private AddProductScene addProductScene;
    private UpdateProductScene updateProductScene;

    public ViewProducts() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadProducts();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageProductsText = new Text("Manage Products");
        manageProductsText.getStyleClass().add("page-title");

        productsTable = new TableView<>();
        productsTable.getStyleClass().add("employees-table");

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<Product, Double> costPriceCol = new TableColumn<>("Supply Price");
        costPriceCol.setCellValueFactory(new PropertyValueFactory<>("costPrice"));

        TableColumn<Product, Double> sellingPriceCol = new TableColumn<>("Unit Price");
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Total Stock");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        productsTable.getColumns().addAll(
                idCol,
                productNameCol,
                categoryCol,
                supplierCol,
                costPriceCol,
                sellingPriceCol,
                quantityCol,
                brandCol
        );

        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productsTable.setPrefHeight(420);
        productsTable.setItems(observableProducts);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search product by name, category, supplier, or brand...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(productsTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageProductsText,
                productsTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Product");
        updateBtn = new Button("Update Product");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addProductScene = new AddProductScene(this);
            addProductScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadProducts() {
        products.clear();
        observableProducts.clear();

        products.addAll(ProductDAO.getAllProducts());
        observableProducts.addAll(products);
    }

    private void updateAction() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a product to update");
            return;
        }

        updateProductScene = new UpdateProductScene(this, selectedProduct);
        updateProductScene.showStage();
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
            String brand = product.getBrand() == null ? "" : product.getBrand().toLowerCase();

            if (productName.contains(query)
                    || categoryName.contains(query)
                    || supplierName.contains(query)
                    || brand.contains(query)) {
                filtered.add(product);
            }
        }

        observableProducts.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
