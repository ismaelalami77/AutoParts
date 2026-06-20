module com.example.autoparts {
    requires javafx.controls;
    requires java.sql;
    requires mysql.connector.j;

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
    exports ManagerView.InventoryManagement;
    exports ManagerView.PurchaseManagement;
    exports ManagerView.TransferManagement;
    exports ManagerView.CustomerManagement;
    exports ManagerView.ReportsManagement;

    opens ManagerView.EmployeeManagement to javafx.base;
    opens ManagerView.SupplierManagement to javafx.base;
    opens ManagerView.CategoryManagement to javafx.base;
    opens ManagerView.ProductManagement to javafx.base;
    opens ManagerView.BranchManagement to javafx.base;
    opens ManagerView.WarehouseManagement to javafx.base;
    opens ManagerView.InventoryManagement to javafx.base;
    opens ManagerView.PurchaseManagement to javafx.base;
    opens ManagerView.TransferManagement to javafx.base;
    opens ManagerView.CustomerManagement to javafx.base;
    opens ManagerView.ReportsManagement to javafx.base;

    opens EmployeeView to javafx.base;
}
