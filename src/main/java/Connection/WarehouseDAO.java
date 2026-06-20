package Connection;

import ManagerView.WarehouseManagement.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class WarehouseDAO {

    public static ArrayList<Warehouse> getAllWarehouses() {
        ArrayList<Warehouse> list = new ArrayList<>();

        String sql = """
                SELECT warehouse_id, warehouse_name, city, address, phone
                FROM Warehouse
                ORDER BY warehouse_id
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Warehouse warehouse = new Warehouse(
                        rs.getInt("warehouse_id"),
                        rs.getString("warehouse_name"),
                        rs.getString("city") + " - " + rs.getString("address"),
                        rs.getString("phone")
                );

                list.add(warehouse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertWarehouse(Warehouse warehouse) {
        String sql = """
                INSERT INTO Warehouse (warehouse_name, city, address, phone)
                VALUES (?, '', ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, warehouse.getWarehouseName());
            ps.setString(2, warehouse.getLocation());
            ps.setString(3, warehouse.getPhone());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateWarehouse(Warehouse warehouse) {
        String sql = """
                UPDATE Warehouse
                SET warehouse_name = ?,
                    address = ?,
                    phone = ?
                WHERE warehouse_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, warehouse.getWarehouseName());
            ps.setString(2, warehouse.getLocation());
            ps.setString(3, warehouse.getPhone());
            ps.setInt(4, warehouse.getWarehouseId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean phoneExists(String phone) {
        return phoneExistsForOtherWarehouse(phone, 0);
    }

    public static boolean phoneExistsForOtherWarehouse(String phone, int warehouseId) {
        String sql = """
                SELECT warehouse_id
                FROM Warehouse
                WHERE phone = ?
                  AND warehouse_id <> ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setInt(2, warehouseId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
