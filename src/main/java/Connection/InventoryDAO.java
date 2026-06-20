package Connection;

import ManagerView.InventoryManagement.InventoryItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class InventoryDAO {

    public static ArrayList<InventoryItem> getBranchInventory() {
        String sql = """
                SELECT b.branch_id,
                       b.branch_name AS place_name,
                       p.product_id,
                       p.product_name,
                       c.category_name,
                       p.brand,
                       bi.quantity,
                       p.reorder_level,
                       bi.last_updated
                FROM Branch_Inventory bi
                JOIN Branch b ON bi.branch_id = b.branch_id
                JOIN Product p ON bi.product_id = p.product_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                ORDER BY b.branch_name, p.product_name
                """;

        return getInventory(sql);
    }

    public static ArrayList<InventoryItem> getWarehouseInventory() {
        String sql = """
                SELECT w.warehouse_id AS branch_id,
                       w.warehouse_name AS place_name,
                       p.product_id,
                       p.product_name,
                       c.category_name,
                       p.brand,
                       wi.quantity,
                       p.reorder_level,
                       wi.last_updated
                FROM Warehouse_Inventory wi
                JOIN Warehouse w ON wi.warehouse_id = w.warehouse_id
                JOIN Product p ON wi.product_id = p.product_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                ORDER BY w.warehouse_name, p.product_name
                """;

        return getInventory(sql);
    }

    private static ArrayList<InventoryItem> getInventory(String sql) {
        ArrayList<InventoryItem> items = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("branch_id"),
                        rs.getString("place_name"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getString("brand"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level"),
                        rs.getDate("last_updated") == null ? null : rs.getDate("last_updated").toLocalDate()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

}
