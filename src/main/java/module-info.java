module com.example.autoparts {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.autoparts to javafx.fxml;
    exports com.example.autoparts;

    exports ManagerView;
    exports Login;
    exports Connection;

    exports ManagerView.EmployeeManagement;
    exports ManagerView.SupplierManagement;
    exports ManagerView.ProductManagement;
    exports ManagerView.CategoryManagement;

    opens ManagerView.EmployeeManagement to javafx.base;
    opens ManagerView.SupplierManagement to javafx.base;
    opens ManagerView.ProductManagement to javafx.base;
    opens ManagerView.CategoryManagement to javafx.base;
}