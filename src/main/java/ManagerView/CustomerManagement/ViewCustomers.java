package ManagerView.CustomerManagement;

import Connection.CustomerDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ViewCustomers {
    private final BorderPane root;
    private final TableView<Customer> table;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final TextField nameField;
    private final TextField phoneField;
    private final TextField addressField;
    private final TextField searchField;

    public ViewCustomers() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        table = createTable();
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> fillForm(selected));

        searchField = UIHelperC.createStyledTextField("Search customers...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        VBox center = new VBox(15, UIHelperC.createTitleText("Customers"), table, searchField);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(25));
        center.getStyleClass().add("employee-content");

        nameField = UIHelperC.createStyledTextField("Customer Name");
        phoneField = UIHelperC.createStyledTextField("Phone");
        addressField = UIHelperC.createStyledTextField("Address");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.add(UIHelperC.createInfoText("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(UIHelperC.createInfoText("Phone:"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(UIHelperC.createInfoText("Address:"), 0, 2);
        form.add(addressField, 1, 2);

        Button addButton = UIHelperC.createStyledButton("Add");
        Button updateButton = UIHelperC.createStyledButton("Update");
        Button clearButton = UIHelperC.createStyledButton("Clear");
        styleSmallActionButton(addButton);
        styleSmallActionButton(updateButton);
        styleSmallActionButton(clearButton);
        addButton.setOnAction(e -> addCustomer());
        updateButton.setOnAction(e -> updateCustomer());
        clearButton.setOnAction(e -> clearForm());

        HBox actionButtons = new HBox(10, addButton, updateButton, clearButton);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setMaxWidth(Double.MAX_VALUE);
        actionButtons.getStyleClass().add("customer-actions");

        VBox left = new VBox(18, UIHelperC.createInfoText("Customer Details"), form, actionButtons);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(25));
        left.getStyleClass().add("employee-side-actions");

        root.setLeft(left);
        root.setCenter(center);

        loadCustomers();
    }

    private TableView<Customer> createTable() {
        TableView<Customer> customerTable = new TableView<>();
        customerTable.getStyleClass().add("employees-table");

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        customerTable.getColumns().addAll(idCol, nameCol, phoneCol, addressCol);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTable.setPrefHeight(460);
        customerTable.setItems(customers);
        return customerTable;
    }

    private void loadCustomers() {
        customers.setAll(CustomerDAO.getAllCustomers());
    }

    private void addCustomer() {
        Customer customer = readForm(0);
        if (customer == null) {
            return;
        }

        if (CustomerDAO.insertCustomer(customer)) {
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Customer added.");
            clearForm();
            loadCustomers();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Customer could not be added.");
        }
    }

    private void updateCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select a customer to update.");
            return;
        }

        Customer customer = readForm(selected.getCustomerId());
        if (customer == null) {
            return;
        }

        if (CustomerDAO.updateCustomer(customer)) {
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Customer updated.");
            clearForm();
            loadCustomers();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Customer could not be updated.");
        }
    }

    private Customer readForm(int customerId) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter customer name.");
            return null;
        }

        if (phone.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter phone number.");
            return null;
        }

        if (!isValidPhone(phone)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Phone number must be 10 digits.");
            return null;
        }

        if (CustomerDAO.phoneExistsForOtherCustomer(phone, customerId)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Another customer already uses this phone number.");
            return null;
        }

        return new Customer(customerId, name, phone, address);
    }

    private void fillForm(Customer customer) {
        if (customer == null) {
            return;
        }

        nameField.setText(customer.getCustomerName());
        phoneField.setText(customer.getPhone());
        addressField.setText(customer.getAddress());
    }

    private void clearForm() {
        table.getSelectionModel().clearSelection();
        nameField.clear();
        phoneField.clear();
        addressField.clear();
    }

    private void filterTable(String text) {
        ArrayList<Customer> allCustomers = CustomerDAO.getAllCustomers();
        if (text == null || text.trim().isEmpty()) {
            customers.setAll(allCustomers);
            return;
        }

        String query = text.trim().toLowerCase();
        customers.setAll(allCustomers.stream()
                .filter(customer -> contains(customer.getCustomerName(), query)
                        || contains(customer.getPhone(), query)
                        || contains(customer.getAddress(), query))
                .toList());
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void styleSmallActionButton(Button button) {
        button.setMinWidth(95);
        button.setPrefWidth(95);
        button.setMaxWidth(95);
    }

    public BorderPane getRoot() {
        loadCustomers();
        return root;
    }
}
