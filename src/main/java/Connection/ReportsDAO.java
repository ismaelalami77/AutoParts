package Connection;

import ManagerView.ReportsManagement.ReportRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ReportsDAO {

    public static ArrayList<ReportRow> getReport(String reportName) {
        return switch (reportName) {
            case "Products In Branches" -> query("""
                    SELECT b.branch_name AS title,
                           p.product_name AS detail,
                           bi.quantity AS value
                    FROM Branch_Inventory bi
                    JOIN Branch b ON bi.branch_id = b.branch_id
                    JOIN Product p ON bi.product_id = p.product_id
                    ORDER BY b.branch_name, p.product_name
                    """);
            case "Products In Warehouses" -> query("""
                    SELECT w.warehouse_name AS title,
                           p.product_name AS detail,
                           wi.quantity AS value
                    FROM Warehouse_Inventory wi
                    JOIN Warehouse w ON wi.warehouse_id = w.warehouse_id
                    JOIN Product p ON wi.product_id = p.product_id
                    ORDER BY w.warehouse_name, p.product_name
                    """);
            case "Out Of Stock Branch Items" -> query("""
                    SELECT b.branch_name AS title,
                           p.product_name AS detail,
                           bi.quantity AS value
                    FROM Branch_Inventory bi
                    JOIN Branch b ON bi.branch_id = b.branch_id
                    JOIN Product p ON bi.product_id = p.product_id
                    WHERE bi.quantity <= 0
                    ORDER BY b.branch_name, p.product_name
                    """);
            case "Below Minimum Stock" -> query("""
                    SELECT b.branch_name AS title,
                           CONCAT(p.product_name, ' minimum ', COALESCE(p.reorder_level, 0)) AS detail,
                           bi.quantity AS value
                    FROM Branch_Inventory bi
                    JOIN Branch b ON bi.branch_id = b.branch_id
                    JOIN Product p ON bi.product_id = p.product_id
                    WHERE bi.quantity <= COALESCE(p.reorder_level, 0)
                    ORDER BY bi.quantity, p.product_name
                    """);
            case "Transfers By Date" -> query("""
                    SELECT DATE_FORMAT(t.transfer_date, '%Y-%m-%d') AS title,
                           CONCAT(w.warehouse_name, ' to ', b.branch_name) AS detail,
                           COALESCE(SUM(ti.quantity), 0) AS value
                    FROM Transfer t
                    LEFT JOIN Warehouse w ON t.warehouse_id = w.warehouse_id
                    LEFT JOIN Branch b ON t.branch_id = b.branch_id
                    LEFT JOIN Transfer_Item ti ON t.transfer_id = ti.transfer_id
                    GROUP BY t.transfer_id, t.transfer_date, w.warehouse_name, b.branch_name
                    ORDER BY t.transfer_date DESC
                    """);
            case "Sales By Branch" -> query("""
                    SELECT b.branch_name AS title,
                           DATE_FORMAT(so.order_date, '%Y-%m') AS detail,
                           COALESCE(SUM(so.total_amount), 0) AS value
                    FROM Sales_Order so
                    LEFT JOIN Branch b ON so.branch_id = b.branch_id
                    WHERE so.status = 'Paid'
                    GROUP BY b.branch_name, DATE_FORMAT(so.order_date, '%Y-%m')
                    ORDER BY detail DESC, value DESC
                    """);
            case "Most Sold Product By Branch" -> query("""
                    SELECT branch_name AS title,
                           product_name AS detail,
                           sold_qty AS value
                    FROM (
                        SELECT b.branch_name,
                               p.product_name,
                               SUM(soi.quantity) AS sold_qty,
                               ROW_NUMBER() OVER (PARTITION BY b.branch_id ORDER BY SUM(soi.quantity) DESC) AS row_num
                        FROM Sales_Order so
                        JOIN Branch b ON so.branch_id = b.branch_id
                        JOIN Sales_Order_Item soi ON so.sales_order_id = soi.sales_order_id
                        JOIN Product p ON soi.product_id = p.product_id
                        WHERE so.status = 'Paid'
                        GROUP BY b.branch_id, b.branch_name, p.product_id, p.product_name
                    ) ranked
                    WHERE row_num = 1
                    ORDER BY branch_name
                    """);
            case "Branch Inventory Value" -> query("""
                    SELECT b.branch_name AS title,
                           'Inventory value' AS detail,
                           SUM(bi.quantity * COALESCE(p.unit_price, 0)) AS value
                    FROM Branch_Inventory bi
                    JOIN Branch b ON bi.branch_id = b.branch_id
                    JOIN Product p ON bi.product_id = p.product_id
                    GROUP BY b.branch_name
                    ORDER BY value DESC
                    """);
            case "Warehouse Inventory Value" -> query("""
                    SELECT w.warehouse_name AS title,
                           'Inventory value' AS detail,
                           SUM(wi.quantity * COALESCE(p.unit_price, 0)) AS value
                    FROM Warehouse_Inventory wi
                    JOIN Warehouse w ON wi.warehouse_id = w.warehouse_id
                    JOIN Product p ON wi.product_id = p.product_id
                    GROUP BY w.warehouse_name
                    ORDER BY value DESC
                    """);
            case "Payments By Customer" -> query("""
                    SELECT c.customer_name AS title,
                           p.payment_method AS detail,
                           SUM(p.amount) AS value
                    FROM Payment p
                    JOIN Sales_Order so ON p.sales_order_id = so.sales_order_id
                    LEFT JOIN Customer c ON so.customer_id = c.customer_id
                    GROUP BY c.customer_name, p.payment_method
                    ORDER BY value DESC
                    """);
            default -> new ArrayList<>();
        };
    }

    private static ArrayList<ReportRow> query(String sql) {
        ArrayList<ReportRow> rows = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new ReportRow(
                        rs.getString("title"),
                        rs.getString("detail"),
                        rs.getDouble("value")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
}
