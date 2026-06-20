package Connection;

import EmployeeView.ReceiptItem;
import EmployeeView.ReceiptSale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReceiptDAO {

    public static ArrayList<ReceiptSale> getSalesByEmployee(int employeeId) {
        ArrayList<ReceiptSale> sales = new ArrayList<>();

        String sql = """
                SELECT so.sales_order_id,
                       c.customer_name,
                       c.phone,
                       so.order_date,
                       so.total_amount,
                       COALESCE(p.payment_method, 'Unknown') AS payment_method
                FROM Sales_Order so
                JOIN Customer c ON so.customer_id = c.customer_id
                LEFT JOIN Payment p ON so.sales_order_id = p.sales_order_id
                WHERE so.employee_id = ?
                ORDER BY so.order_date DESC, so.sales_order_id DESC
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date orderDate = rs.getDate("order_date");
                    LocalDateTime dateTime = orderDate == null ? null : orderDate.toLocalDate().atStartOfDay();

                    sales.add(new ReceiptSale(
                            rs.getInt("sales_order_id"),
                            rs.getString("customer_name"),
                            rs.getString("phone"),
                            dateTime,
                            rs.getDouble("total_amount"),
                            rs.getString("payment_method")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ArrayList<ReceiptItem> getSaleItems(int orderId) {
        ArrayList<ReceiptItem> items = new ArrayList<>();

        String sql = """
                SELECT p.product_name,
                       soi.quantity,
                       soi.unit_price,
                       (COALESCE(soi.quantity, 0) * COALESCE(soi.unit_price, 0)) AS subtotal
                FROM Sales_Order_Item soi
                JOIN Product p ON soi.product_id = p.product_id
                WHERE soi.sales_order_id = ?
                ORDER BY p.product_name
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ReceiptItem(
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("subtotal")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
