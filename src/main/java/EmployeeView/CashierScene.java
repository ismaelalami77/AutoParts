package EmployeeView;

import Connection.CashierDAO;
import Connection.ProductDAO;
import Login.User;
import ManagerView.ProductManagement.Product;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class CashierScene {

    private BorderPane root;

    private FlowPane productsPane;
    private TableView<CartItem> cartTable;

    private ArrayList<Product> products = new ArrayList<>();
    private ObservableList<CartItem> observableCart = FXCollections.observableArrayList();

    private TextField searchField;
    private TextField customerNameField;
    private TextField customerPhoneField;

    private ComboBox<String> paymentMethodCombo;
    private Label totalLabel;

    private Button removeItemBtn;
    private Button clearCartBtn;
    private Button checkoutBtn;

    private User user;

    public CashierScene(User user) {
        this.user = user;

        root = new BorderPane();
        root.setPadding(new Insets(20));

        createContent();
        refreshProducts();
    }

    private void createContent() {
        Text title = UIHelperC.createTitleText("Cashier System");

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(title);
        root.setTop(topBox);

        productsPane = new FlowPane();
        productsPane.setHgap(10);
        productsPane.setVgap(10);
        productsPane.setPadding(new Insets(10));

        ScrollPane productsScroll = new ScrollPane(productsPane);
        productsScroll.setFitToWidth(true);
        productsScroll.setPrefHeight(450);

        searchField = new TextField();
        searchField.setPromptText("Search product...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> showProductButtons(newValue));

        VBox productsBox = new VBox(10);
        productsBox.setPadding(new Insets(10));
        productsBox.getChildren().addAll(
                new Label("Products"),
                searchField,
                productsScroll
        );

        cartTable = createCartTable();

        customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");

        customerPhoneField = new TextField();
        customerPhoneField.setPromptText("Customer Phone");

        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Cash", "Card");
        paymentMethodCombo.setValue("Cash");

        totalLabel = new Label("Total: 0.00");
        totalLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        removeItemBtn = new Button("Remove Item");
        clearCartBtn = new Button("Clear Cart");
        checkoutBtn = new Button("Checkout");

        removeItemBtn.setOnAction(e -> removeSelectedItem());
        clearCartBtn.setOnAction(e -> clearCart());
        checkoutBtn.setOnAction(e -> checkout());

        HBox cartButtons = new HBox(10);
        cartButtons.setAlignment(Pos.CENTER);
        cartButtons.getChildren().addAll(removeItemBtn, clearCartBtn, checkoutBtn);

        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));
        cartBox.getChildren().addAll(
                new Label("Cart"),
                cartTable,
                customerNameField,
                customerPhoneField,
                paymentMethodCombo,
                totalLabel,
                cartButtons
        );

        HBox centerBox = new HBox(15);
        centerBox.getChildren().addAll(productsBox, cartBox);

        HBox.setHgrow(productsBox, Priority.ALWAYS);
        HBox.setHgrow(cartBox, Priority.ALWAYS);

        root.setCenter(centerBox);
    }

    private TableView<CartItem> createCartTable() {
        TableView<CartItem> table = new TableView<>();

        TableColumn<CartItem, Integer> idCol = new TableColumn<>("Product ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        table.getColumns().addAll(idCol, nameCol, qtyCol, priceCol, subtotalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(500);
        table.setPrefHeight(400);
        table.setItems(observableCart);

        return table;
    }

    public void refreshProducts() {
        products.clear();
        products.addAll(ProductDAO.getAllProducts());
        showProductButtons(searchField == null ? "" : searchField.getText());
    }

    private void showProductButtons(String searchText) {
        productsPane.getChildren().clear();

        String query = searchText == null ? "" : searchText.trim().toLowerCase();

        for (Product product : products) {
            String productName = product.getProductName() == null ? "" : product.getProductName().toLowerCase();
            String categoryName = product.getCategoryName() == null ? "" : product.getCategoryName().toLowerCase();

            if (!query.isEmpty()
                    && !productName.contains(query)
                    && !categoryName.contains(query)) {
                continue;
            }

            Button productButton = new Button(
                    product.getProductName()
                            + "\nPrice: " + product.getSellingPrice()
                            + "\nQty: " + product.getQuantity()
            );

            productButton.setPrefSize(160, 100);
            productButton.setWrapText(true);

            if (product.getQuantity() <= 0) {
                productButton.setDisable(true);
                productButton.setText(productButton.getText() + "\nOUT OF STOCK");
            }

            productButton.setOnAction(e -> addProductToCart(product));

            productsPane.getChildren().add(productButton);
        }
    }

    private void addProductToCart(Product product) {
        int currentCartQuantity = getCartQuantity(product.getProductId());

        if (currentCartQuantity + 1 > product.getQuantity()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Not enough quantity in stock!");
            return;
        }

        CartItem existingItem = findCartItem(product.getProductId());

        if (existingItem == null) {
            observableCart.add(new CartItem(product, 1));
        } else {
            existingItem.increaseQuantity();
            cartTable.refresh();
        }

        updateTotal();
    }

    private CartItem findCartItem(int productId) {
        for (CartItem item : observableCart) {
            if (item.getProductId() == productId) {
                return item;
            }
        }

        return null;
    }

    private int getCartQuantity(int productId) {
        for (CartItem item : observableCart) {
            if (item.getProductId() == productId) {
                return item.getQuantity();
            }
        }

        return 0;
    }

    private void removeSelectedItem() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select item from cart!");
            return;
        }

        observableCart.remove(selectedItem);
        updateTotal();
    }

    private void clearCart() {
        observableCart.clear();
        updateTotal();
    }

    private void checkout() {
        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();
        String paymentMethod = paymentMethodCombo.getValue();

        if (observableCart.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Cart is empty!");
            return;
        }

        if (customerName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter customer name!");
            return;
        }

        if (customerPhone.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter customer phone!");
            return;
        }

        for (CartItem item : observableCart) {
            Product product = findProductById(item.getProductId());

            if (product == null || item.getQuantity() > product.getQuantity()) {
                UIHelperC.showAlert(Alert.AlertType.WARNING, "Not enough quantity for product: " + item.getProductName());
                return;
            }
        }

        double total = calculateTotal();

        int customerId = CashierDAO.getOrCreateCustomer(customerName, customerPhone);

        if (customerId == -1) {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Customer could not be saved!");
            return;
        }

        boolean saved = CashierDAO.saveSale(
                customerId,
                user.getId(),
                new ArrayList<>(observableCart),
                total,
                paymentMethod
        );

        if (saved) {
            for (CartItem item : observableCart) {
                ProductDAO.decreaseProductQuantity(item.getProductId(), item.getQuantity());
            }

            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Sale completed successfully!");

            observableCart.clear();
            customerNameField.clear();
            customerPhoneField.clear();
            updateTotal();
            refreshProducts();

        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Sale could not be completed!");
        }
    }

    private Product findProductById(int productId) {
        for (Product product : products) {
            if (product.getProductId() == productId) {
                return product;
            }
        }

        return null;
    }

    private double calculateTotal() {
        double total = 0;

        for (CartItem item : observableCart) {
            total += item.getSubtotal();
        }

        return total;
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: %.2f", calculateTotal()));
    }

    public BorderPane getRoot() {
        return root;
    }
}