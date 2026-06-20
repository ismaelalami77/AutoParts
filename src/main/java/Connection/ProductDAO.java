package Connection;

import ManagerView.ProductManagement.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class ProductDAO {

    private static final int DEFAULT_BRANCH_ID = 1;

    public static ArrayList<Product> getAllProducts() {
        String sql = """
                SELECT p.product_id,
                       p.product_name,
                       p.category_id,
                       c.category_name,
                       COALESCE(MIN(sp.supplier_id), 0) AS supplier_id,
                       COALESCE(MIN(s.supplier_name), '') AS supplier_name,
                       COALESCE(MIN(sp.supply_price), 0) AS supply_price,
                       p.unit_price,
                       p.brand,
                       COALESCE(bi.branch_quantity, 0) + COALESCE(wi.warehouse_quantity, 0) AS quantity
                FROM Product p
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN Supplier_Product sp ON p.product_id = sp.product_id
                LEFT JOIN Supplier s ON sp.supplier_id = s.supplier_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS branch_quantity
                    FROM Branch_Inventory
                    GROUP BY product_id
                ) bi ON p.product_id = bi.product_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS warehouse_quantity
                    FROM Warehouse_Inventory
                    GROUP BY product_id
                ) wi ON p.product_id = wi.product_id
                GROUP BY p.product_id, p.product_name, p.category_id, c.category_name,
                         p.unit_price, p.brand, bi.branch_quantity, wi.warehouse_quantity
                ORDER BY p.product_id
                """;

        return getProductsBySql(sql, -1);
    }

    public static ArrayList<Product> getProductsForBranch(int branchId) {
        if (branchId <= 0) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT p.product_id,
                       p.product_name,
                       p.category_id,
                       c.category_name,
                       COALESCE(MIN(sp.supplier_id), 0) AS supplier_id,
                       COALESCE(MIN(s.supplier_name), '') AS supplier_name,
                       COALESCE(MIN(sp.supply_price), 0) AS supply_price,
                       p.unit_price,
                       p.brand,
                       COALESCE(bi.quantity, 0) AS quantity
                FROM Branch_Inventory bi
                JOIN Product p ON bi.product_id = p.product_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN Supplier_Product sp ON p.product_id = sp.product_id
                LEFT JOIN Supplier s ON sp.supplier_id = s.supplier_id
                WHERE bi.branch_id = ?
                GROUP BY p.product_id, p.product_name, p.category_id, c.category_name,
                         p.unit_price, p.brand, bi.quantity
                ORDER BY p.product_name
                """;

        return getProductsBySql(sql, branchId);
    }

    public static ArrayList<Product> getProductChoices() {
        ArrayList<Product> list = new ArrayList<>();
        String sql = """
                SELECT p.product_id,
                       p.product_name,
                       p.category_id,
                       c.category_name,
                       p.unit_price,
                       p.brand,
                       COALESCE(bi.branch_quantity, 0) + COALESCE(wi.warehouse_quantity, 0) AS quantity
                FROM Product p
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS branch_quantity
                    FROM Branch_Inventory
                    GROUP BY product_id
                ) bi ON p.product_id = bi.product_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS warehouse_quantity
                    FROM Warehouse_Inventory
                    GROUP BY product_id
                ) wi ON p.product_id = wi.product_id
                ORDER BY p.product_name
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        0,
                        "",
                        0,
                        rs.getDouble("unit_price"),
                        rs.getString("brand"),
                        rs.getInt("quantity")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<Product> getProductsForSupplier(int supplierId) {
        ArrayList<Product> list = new ArrayList<>();
        String sql = """
                SELECT p.product_id,
                       p.product_name,
                       p.category_id,
                       c.category_name,
                       sp.supplier_id,
                       s.supplier_name,
                       sp.supply_price,
                       p.unit_price,
                       p.brand,
                       COALESCE(bi.branch_quantity, 0) + COALESCE(wi.warehouse_quantity, 0) AS quantity
                FROM Supplier_Product sp
                JOIN Product p ON sp.product_id = p.product_id
                JOIN Supplier s ON sp.supplier_id = s.supplier_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS branch_quantity
                    FROM Branch_Inventory
                    GROUP BY product_id
                ) bi ON p.product_id = bi.product_id
                LEFT JOIN (
                    SELECT product_id, SUM(quantity) AS warehouse_quantity
                    FROM Warehouse_Inventory
                    GROUP BY product_id
                ) wi ON p.product_id = wi.product_id
                WHERE sp.supplier_id = ?
                ORDER BY p.product_name
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getInt("supplier_id"),
                            rs.getString("supplier_name"),
                            rs.getDouble("supply_price"),
                            rs.getDouble("unit_price"),
                            rs.getString("brand"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<Product> getProductsForWarehouse(int warehouseId) {
        ArrayList<Product> list = new ArrayList<>();
        String sql = """
                SELECT p.product_id,
                       p.product_name,
                       p.category_id,
                       c.category_name,
                       COALESCE(MIN(sp.supplier_id), 0) AS supplier_id,
                       COALESCE(MIN(s.supplier_name), '') AS supplier_name,
                       COALESCE(MIN(sp.supply_price), 0) AS supply_price,
                       p.unit_price,
                       p.brand,
                       wi.quantity
                FROM Warehouse_Inventory wi
                JOIN Product p ON wi.product_id = p.product_id
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN Supplier_Product sp ON p.product_id = sp.product_id
                LEFT JOIN Supplier s ON sp.supplier_id = s.supplier_id
                WHERE wi.warehouse_id = ?
                  AND wi.quantity > 0
                GROUP BY p.product_id, p.product_name, p.category_id, c.category_name,
                         p.unit_price, p.brand, wi.quantity
                ORDER BY p.product_name
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, warehouseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getInt("supplier_id"),
                            rs.getString("supplier_name"),
                            rs.getDouble("supply_price"),
                            rs.getDouble("unit_price"),
                            rs.getString("brand"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static ArrayList<Product> getProductsBySql(String sql, int branchId) {
        ArrayList<Product> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (branchId > 0) {
                ps.setInt(1, branchId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getInt("supplier_id"),
                            rs.getString("supplier_name"),
                            rs.getDouble("supply_price"),
                            rs.getDouble("unit_price"),
                            rs.getString("brand"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertProduct(Product product) {
        String findExistingProductSql = """
                SELECT p.product_id
                FROM Product p
                WHERE LOWER(p.product_name) = LOWER(?)
                  AND p.category_id = ?
                LIMIT 1
                """;

        String updateExistingProductSql = """
                UPDATE Product
                SET unit_price = ?,
                    brand = ?
                WHERE product_id = ?
                """;

        String insertProductSql = """
                INSERT INTO Product
                (product_name, brand, unit_price, reorder_level, category_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        String insertSupplierProductSql = """
                INSERT INTO Supplier_Product
                (supplier_id, product_id, supply_price)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE supply_price = VALUES(supply_price)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement findExistingPs = con.prepareStatement(findExistingProductSql);
                    PreparedStatement updateProductPs = con.prepareStatement(updateExistingProductSql);
                    PreparedStatement productPs = con.prepareStatement(insertProductSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement supplierProductPs = con.prepareStatement(insertSupplierProductSql)
            ) {
                findExistingPs.setString(1, product.getProductName());
                findExistingPs.setInt(2, product.getCategoryId());

                try (ResultSet existingRs = findExistingPs.executeQuery()) {
                    if (existingRs.next()) {
                        int productId = existingRs.getInt("product_id");

                        updateProductPs.setDouble(1, product.getSellingPrice());
                        updateProductPs.setString(2, product.getBrand());
                        updateProductPs.setInt(3, productId);
                        updateProductPs.executeUpdate();

                        supplierProductPs.setInt(1, product.getSupplierId());
                        supplierProductPs.setInt(2, productId);
                        supplierProductPs.setDouble(3, product.getCostPrice());
                        supplierProductPs.executeUpdate();

                        con.commit();
                        return true;
                    }
                }

                productPs.setString(1, product.getProductName());
                productPs.setString(2, product.getBrand());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setInt(4, 5);
                productPs.setInt(5, product.getCategoryId());

                if (productPs.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                int productId;
                try (ResultSet generatedKeys = productPs.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        con.rollback();
                        return false;
                    }
                    productId = generatedKeys.getInt(1);
                }

                supplierProductPs.setInt(1, product.getSupplierId());
                supplierProductPs.setInt(2, productId);
                supplierProductPs.setDouble(3, product.getCostPrice());
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

    public static boolean updateProduct(Product product) {
        String updateProductSql = """
                UPDATE Product
                SET product_name = ?,
                    brand = ?,
                    unit_price = ?,
                    reorder_level = ?,
                    category_id = ?
                WHERE product_id = ?
                """;

        String upsertSupplierProductSql = """
                INSERT INTO Supplier_Product
                (supplier_id, product_id, supply_price)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE supply_price = VALUES(supply_price)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement productPs = con.prepareStatement(updateProductSql);
                    PreparedStatement supplierProductPs = con.prepareStatement(upsertSupplierProductSql)
            ) {
                productPs.setString(1, product.getProductName());
                productPs.setString(2, product.getBrand());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setInt(4, 5);
                productPs.setInt(5, product.getCategoryId());
                productPs.setInt(6, product.getProductId());

                if (productPs.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                supplierProductPs.setInt(1, product.getSupplierId());
                supplierProductPs.setInt(2, product.getProductId());
                supplierProductPs.setDouble(3, product.getCostPrice());
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

    public static boolean deleteProduct(int productId) {
        String deleteProductSql = "DELETE FROM Product WHERE product_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement productPs = con.prepareStatement(deleteProductSql)) {

            productPs.setInt(1, productId);
            return productPs.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decreaseBranchProductQuantity(int branchId, int productId, int soldQuantity) {
        String sql = """
                UPDATE Branch_Inventory
                SET quantity = quantity - ?,
                    last_updated = ?
                WHERE branch_id = ?
                  AND product_id = ?
                  AND quantity >= ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, soldQuantity);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(3, branchId);
            ps.setInt(4, productId);
            ps.setInt(5, soldQuantity);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decreaseProductQuantity(int productId, int soldQuantity) {
        return decreaseBranchProductQuantity(DEFAULT_BRANCH_ID, productId, soldQuantity);
    }

    public static int getBranchProductQuantity(int branchId, int productId) {
        String sql = """
                SELECT quantity
                FROM Branch_Inventory
                WHERE branch_id = ?
                  AND product_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, branchId);
            ps.setInt(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getProductQuantity(int productId) {
        return getBranchProductQuantity(DEFAULT_BRANCH_ID, productId);
    }
}
