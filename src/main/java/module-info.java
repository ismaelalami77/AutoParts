module com.example.autoparts {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.autoparts to javafx.fxml;
    exports com.example.autoparts;
    opens ManagerView.EmployeeManagement to javafx.base;
}