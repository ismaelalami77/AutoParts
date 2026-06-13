package ManagerView.EmployeeManagement;

import Connection.EmployeeDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;

public class ViewEmployees {

    public static ArrayList<Employee> employees = new ArrayList<>();
    public static ObservableList<Employee> observableEmployees = FXCollections.observableArrayList();

    private BorderPane root;
    private TableView<Employee> employeesTable;

    private VBox leftVBox;
    private VBox centerVBox;

    private Text manageEmployeesText;
    private TextField searchTextField;

    private Button updateBtn;
    private Button addBtn;

    private AddEmployeeScene addEmployeeScene;
    private UpdateEmployeeScene updateEmployeeScene;

    public ViewEmployees() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        createCenterContent();
        createLeftButtons();

        root.setLeft(leftVBox);
        root.setCenter(centerVBox);

        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> filterTable(newValue));

        loadEmployees();
    }

    private void createCenterContent() {
        centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(25));
        centerVBox.getStyleClass().add("employee-content");

        manageEmployeesText = new Text("Manage Employees");
        manageEmployeesText.getStyleClass().add("page-title");

        employeesTable = new TableView<>();
        employeesTable.getStyleClass().add("employees-table");

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Employee, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Employee, LocalDate> hireDateCol = new TableColumn<>("Hire Date");
        hireDateCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

        TableColumn<Employee, Integer> branchIdCol = new TableColumn<>("Branch ID");
        branchIdCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        employeesTable.getColumns().addAll(
                idCol,
                fullNameCol,
                positionCol,
                salaryCol,
                phoneCol,
                hireDateCol,
                branchIdCol
        );

        employeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        employeesTable.setPrefHeight(420);
        employeesTable.setItems(observableEmployees);

        searchTextField = new TextField();
        searchTextField.setPromptText("Search employee by name...");
        searchTextField.getStyleClass().add("employee-search-field");

        searchTextField.setMaxWidth(Double.MAX_VALUE);
        searchTextField.prefWidthProperty().bind(employeesTable.widthProperty());

        centerVBox.getChildren().addAll(
                manageEmployeesText,
                employeesTable,
                searchTextField
        );
    }

    private void createLeftButtons() {
        leftVBox = new VBox(18);
        leftVBox.setAlignment(Pos.CENTER);
        leftVBox.setPadding(new Insets(25));
        leftVBox.getStyleClass().add("employee-side-actions");

        addBtn = new Button("Add Employee");
        updateBtn = new Button("Update Employee");

        addBtn.getStyleClass().add("modern-action-button");
        updateBtn.getStyleClass().add("modern-action-button");

        leftVBox.getChildren().addAll(addBtn, updateBtn);

        addBtn.setOnAction(e -> {
            addEmployeeScene = new AddEmployeeScene(this);
            addEmployeeScene.showStage();
        });

        updateBtn.setOnAction(e -> updateAction());
    }

    public void loadEmployees() {
        employees.clear();
        observableEmployees.clear();

        employees.addAll(EmployeeDAO.getAllEmployees());

        observableEmployees.addAll(employees);
    }


    private void updateAction() {
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            UIHelperC.showAlert(Alert.AlertType.WARNING, "Please select an employee to update");
            return;
        }

        updateEmployeeScene = new UpdateEmployeeScene(this, selectedEmployee);
        updateEmployeeScene.showStage();
    }

    private void filterTable(String text) {
        if (text == null || text.trim().isEmpty()) {
            observableEmployees.setAll(employees);
            return;
        }

        String query = text.trim().toLowerCase();
        ArrayList<Employee> filtered = new ArrayList<>();

        for (Employee employee : employees) {
            String fullName = employee.getFullName() == null ? "" : employee.getFullName().toLowerCase();
            String position = employee.getPosition() == null ? "" : employee.getPosition().toLowerCase();
            String phone = employee.getPhone() == null ? "" : employee.getPhone().toLowerCase();

            if (fullName.contains(query) || position.contains(query) || phone.contains(query)) {
                filtered.add(employee);
            }
        }

        observableEmployees.setAll(filtered);
    }

    public BorderPane getRoot() {
        return root;
    }
}
