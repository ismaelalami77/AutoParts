package ManagerView.ProductManagement;

import Connection.ProductDAO;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private TextField categoryIdField;
    private TextField supplierIdField;
    private TextField costPriceField;
    private TextField sellingPriceField;
    private TextField descriptionField;

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
        categoryIdField = UIHelperC.createStyledTextField("Category ID");
        supplierIdField = UIHelperC.createStyledTextField("Supplier ID");
        costPriceField = UIHelperC.createStyledTextField("Cost Price");
        sellingPriceField = UIHelperC.createStyledTextField("Selling Price");
        descriptionField = UIHelperC.createStyledTextField("Description");

        fillFields();

        grid.add(UIHelperC.createInfoText("Product Name:"), 0, 0);
        grid.add(productNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Category ID:"), 0, 1);
        grid.add(categoryIdField, 1, 1);

        grid.add(UIHelperC.createInfoText("Supplier ID:"), 0, 2);
        grid.add(supplierIdField, 1, 2);

        grid.add(UIHelperC.createInfoText("Cost Price:"), 0, 3);
        grid.add(costPriceField, 1, 3);

        grid.add(UIHelperC.createInfoText("Selling Price:"), 0, 4);
        grid.add(sellingPriceField, 1, 4);

        grid.add(UIHelperC.createInfoText("Description:"), 0, 5);
        grid.add(descriptionField, 1, 5);

        updateProductButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateProductButton, cancelButton);

        centerVbox.getChildren().addAll(updateProductText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 750, 650);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Product");

        cancelButton.setOnAction(e -> stage.close());
        updateProductButton.setOnAction(e -> updateProduct());

        productNameField.setOnAction(e -> categoryIdField.requestFocus());
        categoryIdField.setOnAction(e -> supplierIdField.requestFocus());
        supplierIdField.setOnAction(e -> costPriceField.requestFocus());
        costPriceField.setOnAction(e -> sellingPriceField.requestFocus());
        sellingPriceField.setOnAction(e -> descriptionField.requestFocus());
        descriptionField.setOnAction(e -> updateProduct());
    }

    private void fillFields() {
        productNameField.setText(product.getProductName());
        categoryIdField.setText(String.valueOf(product.getCategoryId()));
        supplierIdField.setText(String.valueOf(product.getSupplierId()));
        costPriceField.setText(String.valueOf(product.getCostPrice()));
        sellingPriceField.setText(String.valueOf(product.getSellingPrice()));
        descriptionField.setText(product.getDescription());
    }

    private void updateProduct() {
        String productName = productNameField.getText().trim();
        String categoryIdText = categoryIdField.getText().trim();
        String supplierIdText = supplierIdField.getText().trim();
        String costPriceText = costPriceField.getText().trim();
        String sellingPriceText = sellingPriceField.getText().trim();
        String description = descriptionField.getText().trim();

        if (productName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter product name!");
            return;
        }

        if (categoryIdText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter category ID!");
            return;
        }

        if (supplierIdText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter supplier ID!");
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

        int categoryId;
        int supplierId;
        double costPrice;
        double sellingPrice;

        try {
            categoryId = Integer.parseInt(categoryIdText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Category ID must be a number!");
            return;
        }

        try {
            supplierId = Integer.parseInt(supplierIdText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Supplier ID must be a number!");
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
                product.getCategoryName(),
                supplierId,
                product.getSupplierName(),
                costPrice,
                sellingPrice,
                description
        );

        boolean updated = ProductDAO.updateProduct(updatedProduct);

        if (updated) {
            viewProducts.loadProducts();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Product updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Product could not be updated! Check category ID and supplier ID.");
        }
    }

    public void showStage() {
        stage.show();
    }
}