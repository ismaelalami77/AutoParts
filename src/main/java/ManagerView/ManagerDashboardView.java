package ManagerView;

import Connection.DashboardDAO;
import Connection.DashboardDAO.ChartItem;
import Connection.DashboardDAO.Summary;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ManagerDashboardView {

    private final BorderPane root;
    private final VBox content;
    private final FlowPane summaryPane;
    private final TilePane chartPane;

    public ManagerDashboardView() {
        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        content = new VBox(24);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30));
        content.setMaxWidth(1180);
        content.getStyleClass().add("dashboard-content");

        Label title = new Label("Sales Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Live overview of sales, stock, products, employees, and suppliers");
        subtitle.getStyleClass().add("dashboard-subtitle");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("dashboard-refresh-button");
        refreshButton.setOnAction(e -> loadDashboard());

        VBox header = new VBox(8, title, subtitle, refreshButton);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("dashboard-header");

        summaryPane = new FlowPane(16, 16);
        summaryPane.setAlignment(Pos.CENTER);
        summaryPane.setPrefWrapLength(980);
        summaryPane.setMaxWidth(1020);

        chartPane = new TilePane();
        chartPane.setHgap(18);
        chartPane.setVgap(18);
        chartPane.setAlignment(Pos.CENTER);
        chartPane.setTileAlignment(Pos.TOP_CENTER);
        chartPane.setPrefColumns(2);
        chartPane.setPrefTileWidth(470);
        chartPane.setMaxWidth(980);

        content.getChildren().addAll(header, summaryPane, chartPane);

        StackPane dashboardViewport = new StackPane(content);
        dashboardViewport.setAlignment(Pos.TOP_CENTER);
        dashboardViewport.getStyleClass().add("dashboard-viewport");

        ScrollPane scrollPane = new ScrollPane(dashboardViewport);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dashboardViewport.prefWidthProperty().bind(scrollPane.widthProperty());
        root.setCenter(scrollPane);

        loadDashboard();
    }

    public void loadDashboard() {
        Summary summary = DashboardDAO.getSummary();

        summaryPane.getChildren().clear();
        addSummaryCard("Total Sales", String.format("%.2f", summary.totalSales()), "Paid revenue");
        addSummaryCard("Orders", String.valueOf(summary.orderCount()), "Paid sales");
        addSummaryCard("Units Sold", String.valueOf(summary.unitsSold()), "Paid items sold");
        addSummaryCard("Top Customer", summary.topCustomer(), "Highest paid sales");
        addSummaryCard("Top Product", summary.topProduct(), "Best seller");
        addSummaryCard("Top Category", summary.topCategory(), "Strongest category");
        addSummaryCard("Top Employee", summary.topEmployee(), "Highest paid sales");
        addSummaryCard("Top Supplier", summary.topSupplier(), "Most units sold");

        chartPane.getChildren().clear();
        chartPane.getChildren().addAll(
                createChartPanel("Top Products", createBarChart("Product", "Units", DashboardDAO.getTopProducts())),
                createChartPanel("Sales By Category", createPieChart(DashboardDAO.getCategorySales())),
                createChartPanel("Top Employees", createBarChart("Employee", "Sales", DashboardDAO.getEmployeeSales())),
                createChartPanel("Top Suppliers", createBarChart("Supplier", "Units", DashboardDAO.getSupplierSales()))
        );
    }

    private void addSummaryCard(String title, String value, String caption) {
        VBox card = new VBox(8);
        card.getStyleClass().add("dashboard-card");
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(220);
        card.setPrefWidth(220);
        card.setMinHeight(116);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboard-card-title");

        Label valueLabel = new Label(value == null || value.isBlank() ? "No data" : value);
        valueLabel.getStyleClass().add("dashboard-card-value");
        valueLabel.setWrapText(true);

        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("dashboard-card-caption");

        card.getChildren().addAll(titleLabel, valueLabel, captionLabel);
        summaryPane.getChildren().add(card);
    }

    private VBox createChartPanel(String title, Node chart) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("dashboard-chart-panel");
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(470);
        panel.setMinHeight(340);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboard-chart-title");

        panel.getChildren().addAll(titleLabel, chart);
        VBox.setVgrow(chart, Priority.ALWAYS);

        return panel;
    }

    private BarChart<String, Number> createBarChart(String xLabel, String yLabel, ArrayList<ChartItem> items) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(270);
        chart.setPrefWidth(430);
        xAxis.setTickLabelRotation(-18);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (items.isEmpty()) {
            series.getData().add(new XYChart.Data<>("No data", 0));
        } else {
            for (ChartItem item : items) {
                series.getData().add(new XYChart.Data<>(item.name(), item.value()));
            }
        }

        chart.getData().setAll(series);
        return chart;
    }

    private PieChart createPieChart(ArrayList<ChartItem> items) {
        PieChart chart = new PieChart();
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setPrefHeight(270);
        chart.setPrefWidth(430);

        if (items.isEmpty()) {
            chart.setData(FXCollections.observableArrayList(new PieChart.Data("No data", 1)));
        } else {
            ArrayList<PieChart.Data> data = new ArrayList<>();
            for (ChartItem item : items) {
                data.add(new PieChart.Data(item.name(), item.value()));
            }
            chart.setData(FXCollections.observableArrayList(data));
        }

        return chart;
    }

    public BorderPane getRoot() {
        return root;
    }
}
