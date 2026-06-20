package Connection;

import EmployeeView.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class CashierDAO {

    public static int getOrCreateCustomer(String customerName, String phone) {
        String selectSql = """
                SELECT customer_id
                FROM Customer
                WHERE phone = ?
                """;

        String insertSql = """
                INSERT INTO Customer (customer_name, phone, address)
                VALUES (?, ?, NULL)
                """;

        try (Connection con = DBUtil.getConnection()) {

            try (PreparedStatement selectPs = con.prepareStatement(selectSql)) {
                selectPs.setString(1, phone);

                try (ResultSet rs = selectPs.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("customer_id");
                    }
                }
            }

            try (PreparedStatement insertPs = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setString(1, customerName);
                insertPs.setString(2, phone);

                if (insertPs.executeUpdate() == 0) {
                    return -1;
                }

                try (ResultSet keys = insertPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean saveSale(int customerId, int employeeId, int branchId,
                                   List<CartItem> cartItems,
                                   double totalAmount) {

        String insertOrderSql = """
                INSERT INTO Sales_Order (order_date, total_amount, status, customer_id, employee_id, branch_id)
                VALUES (?, ?, 'Paid', ?, ?, ?)
                """;

        String insertItemSql = """
                INSERT INTO Sales_Order_Item
                (sales_order_id, product_id, quantity, unit_price)
                VALUES (?, ?, ?, ?)
                """;

        String insertPaymentSql = """
                INSERT INTO Payment (payment_date, amount, payment_method, sales_order_id)
                VALUES (?, ?, ?, ?)
                """;

        String decreaseInventorySql = """
                UPDATE Branch_Inventory
                SET quantity = quantity - ?,
                    last_updated = ?
                WHERE branch_id = ?
                  AND product_id = ?
                  AND quantity >= ?
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement orderPs = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement itemPs = con.prepareStatement(insertItemSql);
                    PreparedStatement paymentPs = con.prepareStatement(insertPaymentSql);
                    PreparedStatement inventoryPs = con.prepareStatement(decreaseInventorySql)
            ) {
                LocalDate today = LocalDate.now();

                orderPs.setDate(1, java.sql.Date.valueOf(today));
                orderPs.setDouble(2, totalAmount);
                orderPs.setInt(3, customerId);
                orderPs.setInt(4, employeeId);
                orderPs.setInt(5, branchId);

                if (orderPs.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                int orderId;
                try (ResultSet keys = orderPs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        con.rollback();
                        return false;
                    }
                    orderId = keys.getInt(1);
                }

                for (CartItem item : cartItems) {
                    itemPs.setInt(1, orderId);
                    itemPs.setInt(2, item.getProductId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setDouble(4, item.getUnitPrice());
                    itemPs.addBatch();

                    inventoryPs.setInt(1, item.getQuantity());
                    inventoryPs.setDate(2, java.sql.Date.valueOf(today));
                    inventoryPs.setInt(3, branchId);
                    inventoryPs.setInt(4, item.getProductId());
                    inventoryPs.setInt(5, item.getQuantity());

                    if (inventoryPs.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }

                itemPs.executeBatch();

                paymentPs.setDate(1, java.sql.Date.valueOf(today));
                paymentPs.setDouble(2, totalAmount);
                paymentPs.setString(3, "Paid");
                paymentPs.setInt(4, orderId);

                if (paymentPs.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
