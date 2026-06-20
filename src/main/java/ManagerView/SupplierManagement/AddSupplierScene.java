package ManagerView.SupplierManagement;

import Connection.SupplierDAO;
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

public class AddSupplierScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text addSupplierText;

    private TextField supplierNameField;
    private TextField phoneField;
    private TextField emailField;
    private TextField addressField;

    private Button addSupplierButton;
    private Button cancelButton;

    private final ViewSuppliers viewSuppliers;

    public AddSupplierScene(ViewSuppliers viewSuppliers) {
        this.viewSuppliers = viewSuppliers;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        addSupplierText = UIHelperC.createTitleText("Add Supplier");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        supplierNameField = UIHelperC.createStyledTextField("Supplier Name");
        phoneField = UIHelperC.createStyledTextField("Phone");
        emailField = UIHelperC.createStyledTextField("Email");
        addressField = UIHelperC.createStyledTextField("Address");

        grid.add(UIHelperC.createInfoText("Supplier Name:"), 0, 0);
        grid.add(supplierNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);

        grid.add(UIHelperC.createInfoText("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        grid.add(UIHelperC.createInfoText("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        addSupplierButton = UIHelperC.createStyledButton("Add");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(addSupplierButton, cancelButton);

        centerVbox.getChildren().addAll(addSupplierText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 500);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Add Supplier");

        cancelButton.setOnAction(e -> stage.close());
        addSupplierButton.setOnAction(e -> addSupplier());

        supplierNameField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> addressField.requestFocus());
        addressField.setOnAction(e -> addSupplier());
    }

    private void addSupplier() {
        String supplierName = supplierNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (supplierName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter supplier name!");
            return;
        }

        if (phone.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter phone number!");
            return;
        }

        if (!isValidPhone(phone)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Phone number must be 10 digits!");
            return;
        }

        if (SupplierDAO.phoneExists(phone)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "A supplier with this phone number already exists!");
            return;
        }

        if (email.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter email!");
            return;
        }

        if (!isValidEmail(email)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter a valid email!");
            return;
        }

        if (address.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter address!");
            return;
        }

        Supplier supplier = new Supplier(
                0,
                supplierName,
                phone,
                email,
                address
        );

        boolean inserted = SupplierDAO.insertSupplier(supplier);

        if (inserted) {
            viewSuppliers.loadSuppliers();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Supplier added successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Supplier could not be added!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
