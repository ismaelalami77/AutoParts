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

public class AddProductScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text addProductText;

    private TextField productNameField;
    private ComboBox<Category> categoryComboBox;
    private ComboBox<Supplier> supplierComboBox;
    private TextField costPriceField;
    private TextField sellingPriceField;
    private TextField descriptionField;
    private TextField quantityField;

    private Button addProductButton;
    private Button cancelButton;

    private final ViewProducts viewProducts;

    public AddProductScene(ViewProducts viewProducts) {
        this.viewProducts = viewProducts;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        addProductText = UIHelperC.createTitleText("Add Product");

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

        addProductButton = UIHelperC.createStyledButton("Add");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(addProductButton, cancelButton);

        centerVbox.getChildren().addAll(addProductText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 720, 620);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Add Product");

        cancelButton.setOnAction(e -> stage.close());
        addProductButton.setOnAction(e -> addProduct());

        productNameField.setOnAction(e -> categoryComboBox.requestFocus());
        categoryComboBox.setOnAction(e -> supplierComboBox.requestFocus());
        supplierComboBox.setOnAction(e -> costPriceField.requestFocus());
        costPriceField.setOnAction(e -> sellingPriceField.requestFocus());
        sellingPriceField.setOnAction(e -> quantityField.requestFocus());
        quantityField.setOnAction(e -> descriptionField.requestFocus());
        descriptionField.setOnAction(e -> addProduct());
    }

    private void addProduct() {
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

        if (costPrice < 0 || sellingPrice < 0) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Prices cannot be negative!");
            return;
        }

        Product product = new Product(
                0,
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

        boolean inserted = ProductDAO.insertProduct(product);

        if (inserted) {
            viewProducts.loadProducts();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Product saved successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Product could not be added! Check the selected category and supplier.");
        }
    }

    public void showStage() {
        stage.show();
    }
}
