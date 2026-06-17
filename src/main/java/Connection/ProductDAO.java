package Connection;

import ManagerView.ProductManagement.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductDAO {

    public static ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();

        String sql = """
                SELECT 
                    p.product_id,
                    p.product_name,
                    p.category_id,
                    c.category_name,
                    sp.supplier_id,
                    s.supplier_name,
                    sp.cost_price,
                    p.selling_price,
                    p.description,
                    p.quantity
                FROM Product p
                LEFT JOIN Category c ON p.category_id = c.category_id
                LEFT JOIN Supplier_Product sp ON p.product_id = sp.product_id
                LEFT JOIN Supplier s ON sp.supplier_id = s.supplier_id
                ORDER BY p.product_id
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("selling_price"),
                        rs.getString("description"),
                        rs.getInt("quantity")
                );

                list.add(product);
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
                JOIN Supplier_Product sp ON p.product_id = sp.product_id
                WHERE LOWER(p.product_name) = LOWER(?)
                  AND p.category_id = ?
                  AND sp.supplier_id = ?
                LIMIT 1
                """;

        String updateExistingProductSql = """
                UPDATE Product
                SET selling_price = ?,
                    description = ?,
                    quantity = quantity + ?
                WHERE product_id = ?
                """;

        String updateExistingSupplierProductSql = """
                UPDATE Supplier_Product
                SET cost_price = ?
                WHERE product_id = ?
                  AND supplier_id = ?
                """;

        String insertProductSql = """
                INSERT INTO Product 
                (product_name, category_id, selling_price, description, quantity)
                VALUES (?, ?, ?, ?, ?)
                """;

        String insertSupplierProductSql = """
                INSERT INTO Supplier_Product 
                (supplier_id, product_id, cost_price)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement findExistingPs = con.prepareStatement(findExistingProductSql);
                    PreparedStatement updateProductPs = con.prepareStatement(updateExistingProductSql);
                    PreparedStatement updateSupplierProductPs = con.prepareStatement(updateExistingSupplierProductSql);
                    PreparedStatement productPs = con.prepareStatement(insertProductSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement supplierProductPs = con.prepareStatement(insertSupplierProductSql)
            ) {
                findExistingPs.setString(1, product.getProductName());
                findExistingPs.setInt(2, product.getCategoryId());
                findExistingPs.setInt(3, product.getSupplierId());

                try (ResultSet existingRs = findExistingPs.executeQuery()) {
                    if (existingRs.next()) {
                        int productId = existingRs.getInt("product_id");

                        updateProductPs.setDouble(1, product.getSellingPrice());
                        updateProductPs.setString(2, product.getDescription());
                        updateProductPs.setInt(3, product.getQuantity());
                        updateProductPs.setInt(4, productId);

                        if (updateProductPs.executeUpdate() == 0) {
                            con.rollback();
                            return false;
                        }

                        updateSupplierProductPs.setDouble(1, product.getCostPrice());
                        updateSupplierProductPs.setInt(2, productId);
                        updateSupplierProductPs.setInt(3, product.getSupplierId());

                        if (updateSupplierProductPs.executeUpdate() == 0) {
                            con.rollback();
                            return false;
                        }

                        con.commit();
                        return true;
                    }
                }

                productPs.setString(1, product.getProductName());
                productPs.setInt(2, product.getCategoryId());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setString(4, product.getDescription());
                productPs.setInt(5, product.getQuantity());

                int productAffected = productPs.executeUpdate();

                if (productAffected == 0) {
                    con.rollback();
                    return false;
                }

                int productId;

                try (ResultSet generatedKeys = productPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productId = generatedKeys.getInt(1);
                    } else {
                        con.rollback();
                        return false;
                    }
                }

                supplierProductPs.setInt(1, product.getSupplierId());
                supplierProductPs.setInt(2, productId);
                supplierProductPs.setDouble(3, product.getCostPrice());

                int supplierProductAffected = supplierProductPs.executeUpdate();

                if (supplierProductAffected == 0) {
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

    public static boolean updateProduct(Product product) {
        String updateProductSql = """
                UPDATE Product
                SET product_name = ?,
                    category_id = ?,
                    selling_price = ?,
                    description = ?,
                    quantity = ?
                WHERE product_id = ?
                """;

        String deleteOldSupplierSql = """
                DELETE FROM Supplier_Product
                WHERE product_id = ?
                """;

        String insertSupplierProductSql = """
                INSERT INTO Supplier_Product 
                (supplier_id, product_id, cost_price)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement productPs = con.prepareStatement(updateProductSql);
                    PreparedStatement deletePs = con.prepareStatement(deleteOldSupplierSql);
                    PreparedStatement supplierProductPs = con.prepareStatement(insertSupplierProductSql)
            ) {
                productPs.setString(1, product.getProductName());
                productPs.setInt(2, product.getCategoryId());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setString(4, product.getDescription());
                productPs.setInt(5, product.getQuantity());
                productPs.setInt(6, product.getProductId());

                int productAffected = productPs.executeUpdate();

                if (productAffected == 0) {
                    con.rollback();
                    return false;
                }

                deletePs.setInt(1, product.getProductId());
                deletePs.executeUpdate();

                supplierProductPs.setInt(1, product.getSupplierId());
                supplierProductPs.setInt(2, product.getProductId());
                supplierProductPs.setDouble(3, product.getCostPrice());

                int supplierProductAffected = supplierProductPs.executeUpdate();

                if (supplierProductAffected == 0) {
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

    public static boolean deleteProduct(int productId) {
        String deleteSupplierProductSql = """
                DELETE FROM Supplier_Product
                WHERE product_id = ?
                """;

        String deleteProductSql = """
                DELETE FROM Product
                WHERE product_id = ?
                """;

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement supplierPs = con.prepareStatement(deleteSupplierProductSql);
                    PreparedStatement productPs = con.prepareStatement(deleteProductSql)
            ) {
                supplierPs.setInt(1, productId);
                supplierPs.executeUpdate();

                productPs.setInt(1, productId);
                int affected = productPs.executeUpdate();

                if (affected == 0) {
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

    public static boolean decreaseProductQuantity(int productId, int soldQuantity) {
        String sql = """
                UPDATE Product
                SET quantity = quantity - ?
                WHERE product_id = ?
                  AND quantity >= ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, soldQuantity);
            ps.setInt(2, productId);
            ps.setInt(3, soldQuantity);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getProductQuantity(int productId) {
        String sql = """
                SELECT quantity
                FROM Product
                WHERE product_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
