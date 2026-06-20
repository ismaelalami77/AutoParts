package Connection;

import ManagerView.PurchaseManagement.PurchaseOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class PurchaseOrderDAO {

    public static ArrayList<PurchaseOrder> getAllPurchaseOrders() {
        ArrayList<PurchaseOrder> orders = new ArrayList<>();
        String sql = """
                SELECT po.purchase_order_id,
                       po.order_date,
                       po.total_amount,
                       po.status,
                       s.supplier_name,
                       w.warehouse_name,
                       COUNT(poi.product_id) AS item_count
                FROM Purchase_Order po
                LEFT JOIN Supplier s ON po.supplier_id = s.supplier_id
                LEFT JOIN Warehouse w ON po.warehouse_id = w.warehouse_id
                LEFT JOIN Purchase_Order_Item poi ON po.purchase_order_id = poi.purchase_order_id
                GROUP BY po.purchase_order_id, po.order_date, po.total_amount, po.status,
                         s.supplier_name, w.warehouse_name
                ORDER BY po.purchase_order_id DESC
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new PurchaseOrder(
                        rs.getInt("purchase_order_id"),
                        rs.getDate("order_date") == null ? null : rs.getDate("order_date").toLocalDate(),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("supplier_name"),
                        rs.getString("warehouse_name"),
                        rs.getInt("item_count")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static boolean createPurchaseOrder(int supplierId, int warehouseId, int productId,
                                              int quantity, double unitCost) {
        String insertOrderSql = """
                INSERT INTO Purchase_Order (order_date, total_amount, status, supplier_id, warehouse_id)
                VALUES (?, ?, 'Received', ?, ?)
                """;
        String insertItemSql = """
                INSERT INTO Purchase_Order_Item (purchase_order_id, product_id, quantity, unit_cost)
                VALUES (?, ?, ?, ?)
                """;
        String upsertInventorySql = """
                INSERT INTO Warehouse_Inventory (warehouse_id, product_id, quantity, last_updated)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    quantity = quantity + VALUES(quantity),
                    last_updated = VALUES(last_updated)
                """;
        String upsertSupplierProductSql = """
                INSERT INTO Supplier_Product (supplier_id, product_id, supply_price)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE supply_price = VALUES(supply_price)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement orderPs = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement itemPs = con.prepareStatement(insertItemSql);
                 PreparedStatement inventoryPs = con.prepareStatement(upsertInventorySql);
                 PreparedStatement supplierProductPs = con.prepareStatement(upsertSupplierProductSql)) {

                double total = quantity * unitCost;
                orderPs.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                orderPs.setDouble(2, total);
                orderPs.setInt(3, supplierId);
                orderPs.setInt(4, warehouseId);
                orderPs.executeUpdate();

                int orderId;
                try (ResultSet keys = orderPs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        con.rollback();
                        return false;
                    }
                    orderId = keys.getInt(1);
                }

                itemPs.setInt(1, orderId);
                itemPs.setInt(2, productId);
                itemPs.setInt(3, quantity);
                itemPs.setDouble(4, unitCost);
                itemPs.executeUpdate();

                inventoryPs.setInt(1, warehouseId);
                inventoryPs.setInt(2, productId);
                inventoryPs.setInt(3, quantity);
                inventoryPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                inventoryPs.executeUpdate();

                supplierProductPs.setInt(1, supplierId);
                supplierProductPs.setInt(2, productId);
                supplierProductPs.setDouble(3, unitCost);
                supplierProductPs.executeUpdate();

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
