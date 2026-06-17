package Connection;

import EmployeeView.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class CashierDAO {

    public static int getOrCreateCustomer(String customerName, String phone) {
        String selectSql = """
                SELECT customer_id
                FROM Customer
                WHERE phone = ?
                """;

        String insertSql = """
                INSERT INTO Customer (customer_name, phone)
                VALUES (?, ?)
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

                int affected = insertPs.executeUpdate();

                if (affected == 0) {
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

    public static boolean saveSale(int customerId, int employeeUserId,
                                   List<CartItem> cartItems,
                                   double totalAmount,
                                   String paymentMethod) {

        String insertOrderSql = """
                INSERT INTO Sales_Order (customer_id, employee_user_id, total_amount)
                VALUES (?, ?, ?)
                """;

        String insertItemSql = """
                INSERT INTO Sales_Order_Item
                (order_id, product_id, quantity, unit_price, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        String insertPaymentSql = """
                INSERT INTO Payment (order_id, amount, payment_method)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement orderPs = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement itemPs = con.prepareStatement(insertItemSql);
                    PreparedStatement paymentPs = con.prepareStatement(insertPaymentSql)
            ) {
                orderPs.setInt(1, customerId);
                orderPs.setInt(2, employeeUserId);
                orderPs.setDouble(3, totalAmount);

                int orderAffected = orderPs.executeUpdate();

                if (orderAffected == 0) {
                    con.rollback();
                    return false;
                }

                int orderId;

                try (ResultSet keys = orderPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    } else {
                        con.rollback();
                        return false;
                    }
                }

                for (CartItem item : cartItems) {
                    itemPs.setInt(1, orderId);
                    itemPs.setInt(2, item.getProductId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setDouble(4, item.getUnitPrice());
                    itemPs.setDouble(5, item.getSubtotal());
                    itemPs.addBatch();
                }

                itemPs.executeBatch();

                paymentPs.setInt(1, orderId);
                paymentPs.setDouble(2, totalAmount);
                paymentPs.setString(3, paymentMethod);

                int paymentAffected = paymentPs.executeUpdate();

                if (paymentAffected == 0) {
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