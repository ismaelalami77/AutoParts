package EmployeeView;

import Connection.CashierDAO;
import Connection.CustomerDAO;
import Connection.ProductDAO;
import Login.User;
import ManagerView.CustomerManagement.Customer;
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
    private ComboBox<Customer> customerComboBox;
    private Label customerDetailsLabel;

    private Label totalLabel;

    private Button removeItemBtn;
    private Button clearCartBtn;
    private Button checkoutBtn;

    private User user;

    public CashierScene(User user) {
        this.user = user;

        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("employees-root");

        createContent();
        refreshCustomers();
        refreshProducts();
    }

    private void createContent() {
        Text title = UIHelperC.createTitleText("Cashier");

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.getChildren().add(title);
        root.setTop(topBox);

        productsPane = new FlowPane();
        productsPane.setHgap(10);
        productsPane.setVgap(10);
        productsPane.setPadding(new Insets(10));
        productsPane.setPrefWrapLength(640);

        ScrollPane productsScroll = new ScrollPane(productsPane);
        productsScroll.setFitToWidth(true);
        productsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        productsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        productsScroll.setPrefHeight(450);

        searchField = new TextField();
        searchField.setPromptText("Search product...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> showProductButtons(newValue));

        VBox productsBox = new VBox(10);
        productsBox.setPadding(new Insets(10));
        productsBox.setMinWidth(560);
        productsBox.getStyleClass().add("workspace-panel");
        productsBox.getChildren().addAll(
                new Label("Products"),
                searchField,
                productsScroll
        );
        VBox.setVgrow(productsScroll, Priority.ALWAYS);

        cartTable = createCartTable();

        customerComboBox = new ComboBox<>();
        customerComboBox.setPromptText("Choose Customer");
        customerComboBox.setMaxWidth(Double.MAX_VALUE);
        customerComboBox.setOnAction(e -> showCustomerDetails(customerComboBox.getValue()));

        customerDetailsLabel = new Label("Choose a customer to show details.");
        customerDetailsLabel.getStyleClass().add("form-hint");
        customerDetailsLabel.setWrapText(true);
        customerDetailsLabel.setMinHeight(58);
        customerDetailsLabel.setPrefHeight(58);

        totalLabel = new Label("Total: 0.00");
        totalLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        totalLabel.setMaxWidth(Double.MAX_VALUE);
        totalLabel.setAlignment(Pos.CENTER);

        removeItemBtn = new Button("Remove Item");
        clearCartBtn = new Button("Clear Cart");
        checkoutBtn = new Button("Checkout");

        removeItemBtn.setOnAction(e -> removeSelectedItem());
        clearCartBtn.setOnAction(e -> clearCart());
        checkoutBtn.setOnAction(e -> checkout());

        HBox cartButtons = new HBox(10);
        cartButtons.setAlignment(Pos.CENTER);
        cartButtons.setMaxWidth(Double.MAX_VALUE);
        cartButtons.getStyleClass().add("cashier-actions");
        removeItemBtn.setPrefWidth(130);
        clearCartBtn.setPrefWidth(130);
        checkoutBtn.setPrefWidth(130);
        cartButtons.getChildren().addAll(removeItemBtn, clearCartBtn, checkoutBtn);

        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));
        cartBox.setMinWidth(470);
        cartBox.setPrefWidth(470);
        cartBox.setMaxWidth(470);
        cartBox.getStyleClass().add("workspace-panel");
        cartBox.getChildren().addAll(
                new Label("Cart"),
                cartTable,
                new Label("Customer"),
                customerComboBox,
                customerDetailsLabel,
                totalLabel,
                cartButtons
        );

        HBox centerBox = new HBox(15);
        centerBox.setFillHeight(true);
        centerBox.getChildren().addAll(productsBox, cartBox);

        HBox.setHgrow(productsBox, Priority.ALWAYS);

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
        table.setMinHeight(360);
        table.setPrefHeight(360);
        table.setMaxHeight(360);
        table.setItems(observableCart);

        return table;
    }

    public void refreshProducts() {
        products.clear();
        products.addAll(ProductDAO.getProductsForBranch(user.getBranchId()));
        showProductButtons(searchField == null ? "" : searchField.getText());
    }

    public void refreshCustomers() {
        Customer selectedCustomer = customerComboBox.getValue();
        customerComboBox.getItems().setAll(CustomerDAO.getAllCustomers());

        if (selectedCustomer != null) {
            customerComboBox.getItems().stream()
                    .filter(customer -> customer.getCustomerId() == selectedCustomer.getCustomerId())
                    .findFirst()
                    .ifPresent(customerComboBox::setValue);
        }

        showCustomerDetails(customerComboBox.getValue());
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
            productButton.setMinSize(160, 100);
            productButton.setMaxSize(160, 100);
            productButton.setFocusTraversable(false);
            productButton.setWrapText(true);
            productButton.getStyleClass().add("product-tile");

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
        Customer selectedCustomer = customerComboBox.getValue();

        if (observableCart.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Cart is empty!");
            return;
        }

        if (selectedCustomer == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose a customer!");
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

        boolean saved = CashierDAO.saveSale(
                selectedCustomer.getCustomerId(),
                user.getId(),
                user.getBranchId(),
                new ArrayList<>(observableCart),
                total
        );

        if (saved) {
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Sale completed successfully!");

            observableCart.clear();
            customerComboBox.setValue(null);
            showCustomerDetails(null);
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

    private void showCustomerDetails(Customer customer) {
        if (customer == null) {
            customerDetailsLabel.setText("Choose a customer to show details.");
            return;
        }

        String phone = customer.getPhone() == null || customer.getPhone().isBlank()
                ? "No phone"
                : customer.getPhone();
        String address = customer.getAddress() == null || customer.getAddress().isBlank()
                ? "No address"
                : customer.getAddress();

        customerDetailsLabel.setText(
                "Name: " + customer.getCustomerName()
                        + "\nPhone: " + phone
                        + "\nAddress: " + address
        );
    }

    public BorderPane getRoot() {
        return root;
    }
}
