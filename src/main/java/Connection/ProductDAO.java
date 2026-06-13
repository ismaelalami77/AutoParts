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
                    p.description
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
                        rs.getString("description")
                );

                list.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertProduct(Product product) {
        String insertProductSql = """
                INSERT INTO Product (product_name, category_id, selling_price, description)
                VALUES (?, ?, ?, ?)
                """;

        String insertSupplierProductSql = """
                INSERT INTO Supplier_Product (supplier_id, product_id, cost_price)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement productPs = con.prepareStatement(insertProductSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement supplierProductPs = con.prepareStatement(insertSupplierProductSql)
            ) {
                productPs.setString(1, product.getProductName());
                productPs.setInt(2, product.getCategoryId());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setString(4, product.getDescription());

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
                    description = ?
                WHERE product_id = ?
                """;

        String updateSupplierProductSql = """
                UPDATE Supplier_Product
                SET supplier_id = ?,
                    cost_price = ?
                WHERE product_id = ?
                """;

        String insertSupplierProductSql = """
                INSERT INTO Supplier_Product (supplier_id, product_id, cost_price)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement productPs = con.prepareStatement(updateProductSql);
                    PreparedStatement updateSupplierProductPs = con.prepareStatement(updateSupplierProductSql);
                    PreparedStatement supplierProductPs = con.prepareStatement(insertSupplierProductSql)
            ) {
                productPs.setString(1, product.getProductName());
                productPs.setInt(2, product.getCategoryId());
                productPs.setDouble(3, product.getSellingPrice());
                productPs.setString(4, product.getDescription());
                productPs.setInt(5, product.getProductId());

                int productAffected = productPs.executeUpdate();

                if (productAffected == 0) {
                    con.rollback();
                    return false;
                }

                updateSupplierProductPs.setInt(1, product.getSupplierId());
                updateSupplierProductPs.setDouble(2, product.getCostPrice());
                updateSupplierProductPs.setInt(3, product.getProductId());

                int supplierProductAffected = updateSupplierProductPs.executeUpdate();

                if (supplierProductAffected == 0) {
                    supplierProductPs.setInt(1, product.getSupplierId());
                    supplierProductPs.setInt(2, product.getProductId());
                    supplierProductPs.setDouble(3, product.getCostPrice());

                    supplierProductAffected = supplierProductPs.executeUpdate();
                }


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

}
