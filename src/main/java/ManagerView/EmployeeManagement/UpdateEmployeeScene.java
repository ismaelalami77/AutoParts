package ManagerView.EmployeeManagement;

import Connection.BranchDAO;
import Connection.EmployeeDAO;
import com.example.autoparts.UIHelperC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateEmployeeScene {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private GridPane grid;
    private HBox buttonsHbox;
    private VBox centerVbox;

    private Text updateEmployeeText;

    private TextField fullNameField;
    private TextField positionField;
    private TextField salaryField;
    private TextField phoneField;
    private DatePicker hireDatePicker;
    private TextField branchIdField;

    private Button updateEmployeeButton;
    private Button cancelButton;

    private final ViewEmployees viewEmployees;
    private final Employee employee;

    public UpdateEmployeeScene(ViewEmployees viewEmployees, Employee employee) {
        this.viewEmployees = viewEmployees;
        this.employee = employee;

        root = new BorderPane();

        centerVbox = new VBox(15);
        centerVbox.setAlignment(Pos.CENTER);
        centerVbox.setPadding(new Insets(25));

        updateEmployeeText = UIHelperC.createTitleText("Update Employee");

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        fullNameField = UIHelperC.createStyledTextField("Full Name");
        positionField = UIHelperC.createStyledTextField("Position");
        salaryField = UIHelperC.createStyledTextField("Salary");
        phoneField = UIHelperC.createStyledTextField("Phone");
        hireDatePicker = new DatePicker();
        branchIdField = UIHelperC.createStyledTextField("Branch ID");

        styleHireDatePicker();

        fillFields();

        grid.add(UIHelperC.createInfoText("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);

        grid.add(UIHelperC.createInfoText("Position:"), 0, 1);
        grid.add(positionField, 1, 1);

        grid.add(UIHelperC.createInfoText("Salary:"), 0, 2);
        grid.add(salaryField, 1, 2);

        grid.add(UIHelperC.createInfoText("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);

        grid.add(UIHelperC.createInfoText("Hire Date:"), 0, 4);
        grid.add(hireDatePicker, 1, 4);

        grid.add(UIHelperC.createInfoText("Branch ID:"), 0, 5);
        grid.add(branchIdField, 1, 5);

        updateEmployeeButton = UIHelperC.createStyledButton("Update");
        cancelButton = UIHelperC.createStyledButton("Cancel");

        buttonsHbox = new HBox(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setPadding(new Insets(20));
        buttonsHbox.getChildren().addAll(updateEmployeeButton, cancelButton);

        centerVbox.getChildren().addAll(updateEmployeeText, grid, buttonsHbox);

        root.setCenter(centerVbox);

        stage = new Stage();
        scene = new Scene(root, 700, 650);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/autoparts/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Update Employee");

        cancelButton.setOnAction(e -> stage.close());
        updateEmployeeButton.setOnAction(e -> updateEmployee());

        fullNameField.setOnAction(e -> positionField.requestFocus());
        positionField.setOnAction(e -> salaryField.requestFocus());
        salaryField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> branchIdField.requestFocus());
    }

    private void fillFields() {
        fullNameField.setText(employee.getFullName());
        positionField.setText(employee.getPosition());
        salaryField.setText(String.valueOf(employee.getSalary()));
        phoneField.setText(employee.getPhone());

        if (employee.getHireDate() != null) {
            hireDatePicker.setValue(employee.getHireDate());
        }

        branchIdField.setText(String.valueOf(employee.getBranchId()));
    }

    private void updateEmployee() {
        String fullName = fullNameField.getText().trim();
        String position = positionField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String phone = phoneField.getText().trim();
        String branchIdText = branchIdField.getText().trim();

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

        if (EmployeeDAO.phoneExistsForOtherEmployee(phone, employee.getEmployeeId())) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Another employee already uses this phone number!");
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

        if (!BranchDAO.branchExists(branchId)) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "No branch found with this ID!");
            return;
        }

        Employee updatedEmployee = new Employee(
                employee.getEmployeeId(),
                fullName,
                position,
                salary,
                phone,
                hireDatePicker.getValue(),
                branchId
        );

        boolean updated = EmployeeDAO.updateEmployee(updatedEmployee);

        if (updated) {
            viewEmployees.loadEmployees();
            UIHelperC.showAlert(Alert.AlertType.INFORMATION, "Employee updated successfully!");
            stage.close();
        } else {
            UIHelperC.showAlert(Alert.AlertType.ERROR, "Employee could not be updated!");
        }
    }

    public void showStage() {
        stage.show();
    }

    private void styleHireDatePicker() {
        hireDatePicker.setEditable(false);
        hireDatePicker.setPrefSize(250, 54);
        hireDatePicker.setMinSize(250, 54);
        hireDatePicker.setMaxSize(250, 54);
        hireDatePicker.getEditor().setPrefHeight(54);
        hireDatePicker.getEditor().setMinHeight(54);
        hireDatePicker.getEditor().setMaxHeight(54);
        hireDatePicker.getEditor().setStyle("-fx-padding: 0 12px 0 12px; -fx-font-size: 15px;");
        hireDatePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : DATE_FORMATTER.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                return text == null || text.isBlank() ? null : LocalDate.parse(text, DATE_FORMATTER);
            }
        });
    }

    private boolean isValidFullName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}
