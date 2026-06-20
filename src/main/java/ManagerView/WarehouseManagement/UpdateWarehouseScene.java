package ManagerView.WarehouseManagement;

import Connection.WarehouseDAO;
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

public class UpdateWarehouseScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text updateWarehouseText;

    private TextField warehouseNameField;
    private TextField locationField;
    private TextField phoneField;

    private Button updateWarehouseButton;
    private Button cancelButton;

    private final ViewWarehouses viewWarehouses;
    private final Warehouse warehouse;

    public UpdateWarehouseScene(ViewWarehouses viewWarehouses, Warehouse warehouse) {
        this.viewWarehouses = viewWarehouses;
        this.warehouse = warehouse;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        updateWarehouseText = UIHelperC.createTitleText("Update Warehouse");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        warehouseNameField = UIHelperC.createStyledTextField("Warehouse Name");
        locationField = UIHelperC.createStyledTextField("Location");
        phoneField = UIHelperC.createStyledTextField("Phone");

        fillFields();

        grid.add(UIHelperC.createInfoText("Warehouse Name:"), 0, 0);
        grid.add(warehouseNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Location:"), 0, 1);
        grid.add(locationField, 1, 1);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);

        updateWarehouseButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateWarehouseButton, cancelButton);

        centerVbox.getChildren().addAll(updateWarehouseText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 450);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Warehouse");

        cancelButton.setOnAction(e -> stage.close());
        updateWarehouseButton.setOnAction(e -> updateWarehouse());

        warehouseNameField.setOnAction(e -> locationField.requestFocus());
        locationField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> updateWarehouse());
    }

    private void fillFields() {
        warehouseNameField.setText(warehouse.getWarehouseName());
        locationField.setText(warehouse.getLocation());
        phoneField.setText(warehouse.getPhone());
    }

    private void updateWarehouse() {
        String warehouseName = warehouseNameField.getText().trim();
        String location = locationField.getText().trim();
        String phone = phoneField.getText().trim();

        if (warehouseName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter warehouse name!");
            return;
        }

        if (location.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter location!");
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

        if (WarehouseDAO.phoneExistsForOtherWarehouse(phone, warehouse.getWarehouseId())) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Another warehouse already uses this phone number!");
            return;
        }

        Warehouse updatedWarehouse = new Warehouse(
                warehouse.getWarehouseId(),
                warehouseName,
                location,
                phone
        );

        boolean updated = WarehouseDAO.updateWarehouse(updatedWarehouse);

        if (updated) {
            viewWarehouses.loadWarehouses();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Warehouse updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Warehouse could not be updated!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}
