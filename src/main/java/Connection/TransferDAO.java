package Connection;

import ManagerView.TransferManagement.TransferRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransferDAO {

    public static ArrayList<TransferRecord> getAllTransfers() {
        ArrayList<TransferRecord> transfers = new ArrayList<>();
        String sql = """
                SELECT t.transfer_id,
                       t.transfer_date,
                       t.status,
                       w.warehouse_name,
                       b.branch_name,
                       COUNT(ti.product_id) AS item_count
                FROM Transfer t
                LEFT JOIN Warehouse w ON t.warehouse_id = w.warehouse_id
                LEFT JOIN Branch b ON t.branch_id = b.branch_id
                LEFT JOIN Transfer_Item ti ON t.transfer_id = ti.transfer_id
                GROUP BY t.transfer_id, t.transfer_date, t.status, w.warehouse_name, b.branch_name
                ORDER BY t.transfer_id DESC
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transfers.add(new TransferRecord(
                        rs.getInt("transfer_id"),
                        rs.getDate("transfer_date") == null ? null : rs.getDate("transfer_date").toLocalDate(),
                        rs.getString("status"),
                        rs.getString("warehouse_name"),
                        rs.getString("branch_name"),
                        rs.getInt("item_count")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transfers;
    }

    public static boolean createTransfer(int warehouseId, int branchId, int productId, int quantity) {
        String currentWarehouseQuantitySql = """
                SELECT quantity
                FROM Warehouse_Inventory
                WHERE warehouse_id = ?
                  AND product_id = ?
                """;
        String insertTransferSql = """
                INSERT INTO Transfer (transfer_date, status, warehouse_id, branch_id)
                VALUES (?, 'Completed', ?, ?)
                """;
        String insertItemSql = """
                INSERT INTO Transfer_Item (transfer_id, product_id, quantity)
                VALUES (?, ?, ?)
                """;
        String decreaseWarehouseSql = """
                UPDATE Warehouse_Inventory
                SET quantity = quantity - ?,
                    last_updated = ?
                WHERE warehouse_id = ?
                  AND product_id = ?
                  AND quantity >= ?
                """;
        String upsertBranchSql = """
                INSERT INTO Branch_Inventory (branch_id, product_id, quantity, last_updated)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    quantity = quantity + VALUES(quantity),
                    last_updated = VALUES(last_updated)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement currentPs = con.prepareStatement(currentWarehouseQuantitySql);
                 PreparedStatement transferPs = con.prepareStatement(insertTransferSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement itemPs = con.prepareStatement(insertItemSql);
                 PreparedStatement decreasePs = con.prepareStatement(decreaseWarehouseSql);
                 PreparedStatement branchPs = con.prepareStatement(upsertBranchSql)) {

                currentPs.setInt(1, warehouseId);
                currentPs.setInt(2, productId);
                try (ResultSet rs = currentPs.executeQuery()) {
                    if (!rs.next() || rs.getInt("quantity") < quantity) {
                        con.rollback();
                        return false;
                    }
                }

                transferPs.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                transferPs.setInt(2, warehouseId);
                transferPs.setInt(3, branchId);
                transferPs.executeUpdate();

                int transferId;
                try (ResultSet keys = transferPs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        con.rollback();
                        return false;
                    }
                    transferId = keys.getInt(1);
                }

                itemPs.setInt(1, transferId);
                itemPs.setInt(2, productId);
                itemPs.setInt(3, quantity);
                itemPs.executeUpdate();

                decreasePs.setInt(1, quantity);
                decreasePs.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                decreasePs.setInt(3, warehouseId);
                decreasePs.setInt(4, productId);
                decreasePs.setInt(5, quantity);
                if (decreasePs.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                branchPs.setInt(1, branchId);
                branchPs.setInt(2, productId);
                branchPs.setInt(3, quantity);
                branchPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                branchPs.executeUpdate();

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
