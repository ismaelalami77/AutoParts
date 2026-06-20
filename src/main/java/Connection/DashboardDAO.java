package Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DashboardDAO {

    public record Summary(String topProduct, String topCategory, String topEmployee,
                          String topSupplier, String topCustomer, double totalSales,
                          int orderCount, int unitsSold) {
    }

    public record ChartItem(String name, double value) {
    }

    public static Summary getSummary() {
        return new Summary(
                getTopName("""
                        SELECT p.product_name AS name
                        FROM Sales_Order_Item soi
                        JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                        JOIN Product p ON soi.product_id = p.product_id
                        WHERE so.status = 'Paid'
                        GROUP BY p.product_id, p.product_name
                        ORDER BY SUM(soi.quantity) DESC
                        LIMIT 1
                        """),
                getTopName("""
                        SELECT c.category_name AS name
                        FROM Sales_Order_Item soi
                        JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                        JOIN Product p ON soi.product_id = p.product_id
                        LEFT JOIN Category c ON p.category_id = c.category_id
                        WHERE so.status = 'Paid'
                        GROUP BY c.category_id, c.category_name
                        ORDER BY SUM(soi.quantity) DESC
                        LIMIT 1
                        """),
                getTopName("""
                        SELECT e.full_name AS name
                        FROM Sales_Order so
                        LEFT JOIN Employee e ON so.employee_id = e.employee_id
                        WHERE so.status = 'Paid'
                        GROUP BY e.employee_id, e.full_name
                        ORDER BY SUM(so.total_amount) DESC
                        LIMIT 1
                        """),
                getTopName("""
                        SELECT s.supplier_name AS name
                        FROM Sales_Order_Item soi
                        JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                        JOIN Supplier_Product sp ON soi.product_id = sp.product_id
                        JOIN Supplier s ON sp.supplier_id = s.supplier_id
                        WHERE so.status = 'Paid'
                        GROUP BY s.supplier_id, s.supplier_name
                        ORDER BY SUM(soi.quantity) DESC
                        LIMIT 1
                        """),
                getTopName("""
                        SELECT c.customer_name AS name
                        FROM Sales_Order so
                        LEFT JOIN Customer c ON so.customer_id = c.customer_id
                        WHERE so.status = 'Paid'
                        GROUP BY c.customer_id, c.customer_name
                        ORDER BY SUM(so.total_amount) DESC
                        LIMIT 1
                        """),
                getDoubleValue("SELECT COALESCE(SUM(total_amount), 0) AS value FROM Sales_Order WHERE status = 'Paid'"),
                getIntValue("SELECT COUNT(*) AS value FROM Sales_Order WHERE status = 'Paid'"),
                getIntValue("""
                        SELECT COALESCE(SUM(soi.quantity), 0) AS value
                        FROM Sales_Order_Item soi
                        JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                        WHERE so.status = 'Paid'
                        """)
        );
    }

    public static ArrayList<ChartItem> getTopProducts() {
        return getChartItems("""
                SELECT p.product_name AS name, SUM(soi.quantity) AS value
                FROM Sales_Order_Item soi
                JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                JOIN Product p ON soi.product_id = p.product_id
                WHERE so.status = 'Paid'
                GROUP BY p.product_id, p.product_name
                ORDER BY value DESC
                LIMIT 5
                """);
    }

    public static ArrayList<ChartItem> getCategorySales() {
        return getChartItems("""
                SELECT COALESCE(c.category_name, 'Uncategorized') AS name,
                       SUM(COALESCE(soi.quantity, 0) * COALESCE(soi.unit_price, 0)) AS value
                FROM Sales_Order_Item soi
                JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                JOIN Product p ON soi.product_id = p.product_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                WHERE so.status = 'Paid'
                GROUP BY c.category_id, c.category_name
                ORDER BY value DESC
                LIMIT 6
                """);
    }

    public static ArrayList<ChartItem> getEmployeeSales() {
        return getChartItems("""
                SELECT COALESCE(e.full_name, 'No employee') AS name, SUM(so.total_amount) AS value
                FROM Sales_Order so
                LEFT JOIN Employee e ON so.employee_id = e.employee_id
                WHERE so.status = 'Paid'
                GROUP BY e.employee_id, e.full_name
                ORDER BY value DESC
                LIMIT 5
                """);
    }

    public static ArrayList<ChartItem> getSupplierSales() {
        return getChartItems("""
                SELECT s.supplier_name AS name, SUM(soi.quantity) AS value
                FROM Sales_Order_Item soi
                JOIN Sales_Order so ON soi.sales_order_id = so.sales_order_id
                JOIN Supplier_Product sp ON soi.product_id = sp.product_id
                JOIN Supplier s ON sp.supplier_id = s.supplier_id
                WHERE so.status = 'Paid'
                GROUP BY s.supplier_id, s.supplier_name
                ORDER BY value DESC
                LIMIT 5
                """);
    }

    private static String getTopName(String sql) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String name = rs.getString("name");
                return name == null || name.isBlank() ? "No data" : name;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No data";
    }

    private static int getIntValue(String sql) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("value");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static double getDoubleValue(String sql) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("value");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static ArrayList<ChartItem> getChartItems(String sql) {
        ArrayList<ChartItem> items = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                items.add(new ChartItem(name == null || name.isBlank() ? "No name" : name, rs.getDouble("value")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
