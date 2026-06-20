package ManagerView.ReportsManagement;

import Connection.ReportsDAO;
import com.example.autoparts.UIHelperC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewReports {
    private final BorderPane root;
    private final ObservableList<ReportRow> rows = FXCollections.observableArrayList();
    private final ComboBox<String> reportComboBox;

    public ViewReports() {
        root = new BorderPane();
        root.getStyleClass().add("employees-root");

        reportComboBox = new ComboBox<>();
        reportComboBox.getItems().addAll(
                "Products In Branches",
                "Products In Warehouses",
                "Out Of Stock Branch Items",
                "Below Minimum Stock",
                "Transfers By Date",
                "Sales By Branch",
                "Most Sold Product By Branch",
                "Branch Inventory Value",
                "Warehouse Inventory Value",
                "Payments By Customer"
        );
        reportComboBox.setValue("Products In Branches");
        reportComboBox.setPrefSize(280, 44);
        reportComboBox.setOnAction(e -> loadReport());

        TableView<ReportRow> table = new TableView<>();
        table.getStyleClass().add("employees-table");

        TableColumn<ReportRow, String> titleCol = new TableColumn<>("Main");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<ReportRow, String> detailCol = new TableColumn<>("Details");
        detailCol.setCellValueFactory(new PropertyValueFactory<>("detail"));

        TableColumn<ReportRow, Double> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.getColumns().addAll(titleCol, detailCol, valueCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(520);
        table.setItems(rows);

        HBox toolbar = new HBox(12, UIHelperC.createInfoText("Report:"), reportComboBox);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15, UIHelperC.createTitleText("Reports"), toolbar, table);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.getStyleClass().add("employee-content");

        root.setCenter(content);
        loadReport();
    }

    private void loadReport() {
        rows.setAll(ReportsDAO.getReport(reportComboBox.getValue()));
    }

    public BorderPane getRoot() {
        loadReport();
        return root;
    }
}
