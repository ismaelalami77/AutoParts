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

public class AddWarehouseScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text addWarehouseText;

    private TextField warehouseNameField;
    private TextField locationField;
    private TextField phoneField;

    private Button addWarehouseButton;
    private Button cancelButton;

    private final ViewWarehouses viewWarehouses;

    public AddWarehouseScene(ViewWarehouses viewWarehouses) {
        this.viewWarehouses = viewWarehouses;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        addWarehouseText = UIHelperC.createTitleText("Add Warehouse");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        warehouseNameField = UIHelperC.createStyledTextField("Warehouse Name");
        locationField = UIHelperC.createStyledTextField("Location");
        phoneField = UIHelperC.createStyledTextField("Phone");

        grid.add(UIHelperC.createInfoText("Warehouse Name:"), 0, 0);
        grid.add(warehouseNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Location:"), 0, 1);
        grid.add(locationField, 1, 1);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);

        addWarehouseButton = UIHelperC.createStyledButton("Add");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(addWarehouseButton, cancelButton);

        centerVbox.getChildren().addAll(addWarehouseText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 450);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Add Warehouse");

        cancelButton.setOnAction(e -> stage.close());
        addWarehouseButton.setOnAction(e -> addWarehouse());

        warehouseNameField.setOnAction(e -> locationField.requestFocus());
        locationField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> addWarehouse());
    }

    private void addWarehouse() {
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

        Warehouse warehouse = new Warehouse(
                0,
                warehouseName,
                location,
                phone
        );

        boolean inserted = WarehouseDAO.insertWarehouse(warehouse);

        if (inserted) {
            viewWarehouses.loadWarehouses();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Warehouse added successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Warehouse could not be added!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}