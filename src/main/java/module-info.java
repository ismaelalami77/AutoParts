module com.example.autoparts {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.autoparts to javafx.fxml;
    exports com.example.autoparts;

    exports Login;
    exports Connection;
    exports ManagerView;
    exports EmployeeView;

    exports ManagerView.EmployeeManagement;
    exports ManagerView.SupplierManagement;
    exports ManagerView.CategoryManagement;
    exports ManagerView.ProductManagement;
    exports ManagerView.BranchManagement;
    exports ManagerView.WarehouseManagement;

    opens ManagerView.EmployeeManagement to javafx.base;
    opens ManagerView.SupplierManagement to javafx.base;
    opens ManagerView.CategoryManagement to javafx.base;
    opens ManagerView.ProductManagement to javafx.base;
    opens ManagerView.BranchManagement to javafx.base;
    opens ManagerView.WarehouseManagement to javafx.base;

    opens EmployeeView to javafx.base;
}