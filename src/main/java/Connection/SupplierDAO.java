package Connection;

import ManagerView.SupplierManagement.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SupplierDAO {

    public static ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> list = new ArrayList<>();

        String sql = """
                SELECT supplier_id, supplier_name, phone, email, address
                FROM Supplier
                ORDER BY supplier_id
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                );

                list.add(supplier);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertSupplier(Supplier supplier) {
        String sql = """
                INSERT INTO Supplier (supplier_name, phone, email, address)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateSupplier(Supplier supplier) {
        String sql = """
                UPDATE Supplier
                SET supplier_name = ?,
                    phone = ?,
                    email = ?,
                    address = ?
                WHERE supplier_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());
            ps.setInt(5, supplier.getSupplierId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
