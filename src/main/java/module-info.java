module com.example.autoparts {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.autoparts to javafx.fxml;
    exports com.example.autoparts;
}