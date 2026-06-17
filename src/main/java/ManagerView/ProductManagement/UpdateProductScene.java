package ManagerView.ProductManagement;

import Connection.CategoryDAO;
import Connection.ProductDAO;
import Connection.SupplierDAO;
import ManagerView.CategoryManagement.Category;
import ManagerView.SupplierManagement.Supplier;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UpdateProductScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text updateProductText;

    private TextField productNameField;
    private ComboBox<Category> categoryComboBox;
    private ComboBox<Supplier> supplierComboBox;
    private TextField costPriceField;
    private TextField sellingPriceField;
    private TextField descriptionField;
    private TextField quantityField;

    private Button updateProductButton;
    private Button cancelButton;

    private final ViewProducts viewProducts;
    private final Product product;

    public UpdateProductScene(ViewProducts viewProducts, Product product) {
        this.viewProducts = viewProducts;
        this.product = product;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        updateProductText = UIHelperC.createTitleText("Update Product");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        productNameField = UIHelperC.createStyledTextField("Product Name");
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Choose Category");
        categoryComboBox.getItems().addAll(CategoryDAO.getAllCategories());
        categoryComboBox.setPrefSize(250, 44);
        categoryComboBox.setMaxWidth(250);

        supplierComboBox = new ComboBox<>();
        supplierComboBox.setPromptText("Choose Supplier");
        supplierComboBox.getItems().addAll(SupplierDAO.getAllSuppliers());
        supplierComboBox.setPrefSize(250, 44);
        supplierComboBox.setMaxWidth(250);

        costPriceField = UIHelperC.createStyledTextField("Cost Price");
        sellingPriceField = UIHelperC.createStyledTextField("Selling Price");
        quantityField = UIHelperC.createStyledTextField("Quantity");
        descriptionField = UIHelperC.createStyledTextField("Description");

        fillFields();

        grid.add(UIHelperC.createInfoText("Product Name:"), 0, 0);
        grid.add(productNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Category:"), 0, 1);
        grid.add(categoryComboBox, 1, 1);

        grid.add(UIHelperC.createInfoText("Supplier:"), 0, 2);
        grid.add(supplierComboBox, 1, 2);

        grid.add(UIHelperC.createInfoText("Cost Price:"), 0, 3);
        grid.add(costPriceField, 1, 3);

        grid.add(UIHelperC.createInfoText("Selling Price:"), 0, 4);
        grid.add(sellingPriceField, 1, 4);

        grid.add(UIHelperC.createInfoText("Quantity:"), 0, 5);
        grid.add(quantityField, 1, 5);

        grid.add(UIHelperC.createInfoText("Description:"), 0, 6);
        grid.add(descriptionField, 1, 6);

        updateProductButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateProductButton, cancelButton);

        centerVbox.getChildren().addAll(updateProductText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 720, 620);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Product");

        cancelButton.setOnAction(e -> stage.close());
        updateProductButton.setOnAction(e -> updateProduct());

        productNameField.setOnAction(e -> categoryComboBox.requestFocus());
        categoryComboBox.setOnAction(e -> supplierComboBox.requestFocus());
        supplierComboBox.setOnAction(e -> costPriceField.requestFocus());
        costPriceField.setOnAction(e -> sellingPriceField.requestFocus());
        sellingPriceField.setOnAction(e -> quantityField.requestFocus());
        quantityField.setOnAction(e -> descriptionField.requestFocus());
        descriptionField.setOnAction(e -> updateProduct());
    }

    private void fillFields() {
        productNameField.setText(product.getProductName());
        selectCategory(product.getCategoryId());
        selectSupplier(product.getSupplierId());
        costPriceField.setText(String.valueOf(product.getCostPrice()));
        sellingPriceField.setText(String.valueOf(product.getSellingPrice()));
        descriptionField.setText(product.getDescription());
        quantityField.setText(String.valueOf(product.getQuantity()));
    }

    private void updateProduct() {
        String productName = productNameField.getText().trim();
        Category selectedCategory = categoryComboBox.getValue();
        Supplier selectedSupplier = supplierComboBox.getValue();
        String costPriceText = costPriceField.getText().trim();
        String sellingPriceText = sellingPriceField.getText().trim();
        String description = descriptionField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter quantity!");
            return;
        }

        if (productName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter product name!");
            return;
        }

        if (selectedCategory == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose a category!");
            return;
        }

        if (selectedSupplier == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose a supplier!");
            return;
        }

        if (costPriceText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter cost price!");
            return;
        }

        if (sellingPriceText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter selling price!");
            return;
        }

        int categoryId = selectedCategory.getCategoryId();
        int supplierId = selectedSupplier.getSupplierId();
        double costPrice;
        double sellingPrice;
        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity must be a number!");
            return;
        }

        if (quantity < 0) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Quantity cannot be negative!");
            return;
        }

        try {
            costPrice = Double.parseDouble(costPriceText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Cost price must be a number!");
            return;
        }

        try {
            sellingPrice = Double.parseDouble(sellingPriceText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Selling price must be a number!");
            return;
        }

        if (costPrice < 0 || sellingPrice < 0) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Prices cannot be negative!");
            return;
        }

        Product updatedProduct = new Product(
                product.getProductId(),
                productName,
                categoryId,
                selectedCategory.getCategoryName(),
                supplierId,
                selectedSupplier.getSupplierName(),
                costPrice,
                sellingPrice,
                description,
                quantity
        );

        boolean updated = ProductDAO.updateProduct(updatedProduct);

        if (updated) {
            viewProducts.loadProducts();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Product updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Product could not be updated! Check the selected category and supplier.");
        }
    }

    private void selectCategory(int categoryId) {
        for (Category category : categoryComboBox.getItems()) {
            if (category.getCategoryId() == categoryId) {
                categoryComboBox.setValue(category);
                return;
            }
        }
    }

    private void selectSupplier(int supplierId) {
        for (Supplier supplier : supplierComboBox.getItems()) {
            if (supplier.getSupplierId() == supplierId) {
                supplierComboBox.setValue(supplier);
                return;
            }
        }
    }

    public void showStage() {
        stage.show();
    }
}
