package ManagerView.ReportsManagement;

public class ReportRow {
    private String title;
    private String detail;
    private double value;

    public ReportRow(String title, String detail, double value) {
        this.title = title;
        this.detail = detail;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public double getValue() {
        return value;
    }
}
