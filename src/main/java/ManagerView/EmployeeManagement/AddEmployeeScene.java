package ManagerView.EmployeeManagement;

import Connection.EmployeeDAO;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AddEmployeeScene {

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text addEmployeeText;

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fullNameField;
    private TextField positionField;
    private TextField salaryField;
    private TextField phoneField;
    private DatePicker hireDatePicker;
    private TextField branchIdField;

    private Button addEmployeeButton;
    private Button cancelButton;

    private final ViewEmployees viewEmployees;

    public AddEmployeeScene(ViewEmployees viewEmployees) {
        this.viewEmployees = viewEmployees;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        addEmployeeText = UIHelperC.createTitleText("Add Employee");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        usernameField = UIHelperC.createStyledTextField("Username");
        passwordField = UIHelperC.createStyledPassField("Password");
        fullNameField = UIHelperC.createStyledTextField("Full Name");
        positionField = UIHelperC.createStyledTextField("Position");
        salaryField = UIHelperC.createStyledTextField("Salary");
        phoneField = UIHelperC.createStyledTextField("Phone");
        hireDatePicker = new DatePicker();
        branchIdField = UIHelperC.createStyledTextField("Branch ID");

        hireDatePicker.setPrefSize(250, 50);

        grid.add(UIHelperC.createInfoText("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        grid.add(UIHelperC.createInfoText("Full Name:"), 0, 2);
        grid.add(fullNameField, 1, 2);

        grid.add(UIHelperC.createInfoText("Position:"), 0, 3);
        grid.add(positionField, 1, 3);

        grid.add(UIHelperC.createInfoText("Salary:"), 0, 4);
        grid.add(salaryField, 1, 4);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);

        grid.add(UIHelperC.createInfoText("Hire Date:"), 0, 6);
        grid.add(hireDatePicker, 1, 6);

        grid.add(UIHelperC.createInfoText("Branch ID:"), 0, 7);
        grid.add(branchIdField, 1, 7);

        addEmployeeButton = UIHelperC.createStyledButton("Add");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(addEmployeeButton, cancelButton);

        centerVbox.getChildren().addAll(addEmployeeText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 700);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Add Employee");

        cancelButton.setOnAction(e -> stage.close());
        addEmployeeButton.setOnAction(e -> addEmployee());

        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> fullNameField.requestFocus());
        fullNameField.setOnAction(e -> positionField.requestFocus());
        positionField.setOnAction(e -> salaryField.requestFocus());
        salaryField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> branchIdField.requestFocus());
    }

    private void addEmployee() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String position = positionField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String phone = phoneField.getText().trim();
        String branchIdText = branchIdField.getText().trim();

        if (username.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter username!");
            return;
        }

        if (password.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter password!");
            return;
        }

        if (fullName.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter full name!");
            return;
        }

        if (!isValidFullName(fullName)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter a valid full name!");
            return;
        }

        if (position.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter position!");
            return;
        }

        if (salaryText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter salary!");
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

        if (hireDatePicker.getValue() == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please choose hire date!");
            return;
        }

        if (branchIdText.isEmpty()) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please enter branch ID!");
            return;
        }

        double salary;
        int branchId;

        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Salary must be a number!");
            return;
        }

        try {
            branchId = Integer.parseInt(branchIdText);
        } catch (NumberFormatException e) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Branch ID must be a number!");
            return;
        }

        Employee newEmployee = new Employee(
                0,
                fullName,
                position,
                salary,
                phone,
                hireDatePicker.getValue(),
                branchId
        );

        boolean inserted = EmployeeDAO.insertEmployee(username, password, newEmployee);

        if (inserted) {
            viewEmployees.loadEmployees();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Employee added successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Employee could not be added!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private boolean isValidFullName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}