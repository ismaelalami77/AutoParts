package ManagerView.BranchManagement;

import Connection.BranchDAO;
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

public class UpdateBranchScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text updateBranchText;

    private TextField branchNameField;
    private TextField cityField;
    private TextField addressField;
    private TextField phoneField;

    private Button updateBranchButton;
    private Button cancelButton;

    private final ViewBranches viewBranches;
    private final Branch branch;

    public UpdateBranchScene(ViewBranches viewBranches, Branch branch) {
        this.viewBranches = viewBranches;
        this.branch = branch;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        updateBranchText = UIHelperC.createTitleText("Update Branch");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        branchNameField = UIHelperC.createStyledTextField("Branch Name");
        cityField = UIHelperC.createStyledTextField("City");
        addressField = UIHelperC.createStyledTextField("Address");
        phoneField = UIHelperC.createStyledTextField("Phone");

        fillFields();

        grid.add(UIHelperC.createInfoText("Branch Name:"), 0, 0);
        grid.add(branchNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("City:"), 0, 1);
        grid.add(cityField, 1, 1);

        grid.add(UIHelperC.createInfoText("Address:"), 0, 2);
        grid.add(addressField, 1, 2);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);

        updateBranchButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateBranchButton, cancelButton);

        centerVbox.getChildren().addAll(updateBranchText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 500);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Branch");

        cancelButton.setOnAction(e -> stage.close());
        updateBranchButton.setOnAction(e -> updateBranch());

        branchNameField.setOnAction(e -> cityField.requestFocus());
        cityField.setOnAction(e -> addressField.requestFocus());
        addressField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> updateBranch());
    }

    private void fillFields() {
        branchNameField.setText(branch.getBranchName());
        cityField.setText(branch.getCity());
        addressField.setText(branch.getAddress());
        phoneField.setText(branch.getPhone());
    }

    private void updateBranch() {
        String branchName = branchNameField.getText().trim();
        String city = cityField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        if (branchName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter branch name!");
            return;
        }

        if (city.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter city!");
            return;
        }

        if (address.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter address!");
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

        if (BranchDAO.phoneExistsForOtherBranch(phone, branch.getBranchId())) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Another branch already uses this phone number!");
            return;
        }

        Branch updatedBranch = new Branch(
                branch.getBranchId(),
                branchName,
                city,
                address,
                phone
        );

        boolean updated = BranchDAO.updateBranch(updatedBranch);

        if (updated) {
            viewBranches.loadBranches();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Branch updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Branch could not be updated!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}
