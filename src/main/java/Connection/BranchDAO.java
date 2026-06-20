package Connection;

import ManagerView.BranchManagement.Branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BranchDAO {

    public static ArrayList<Branch> getAllBranches() {
        ArrayList<Branch> list = new ArrayList<>();

        String sql = """
                SELECT branch_id, branch_name, city, address, phone
                FROM Branch
                ORDER BY branch_id
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Branch branch = new Branch(
                        rs.getInt("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("city"),
                        rs.getString("address"),
                        rs.getString("phone")
                );

                list.add(branch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertBranch(Branch branch) {
        String sql = """
            INSERT INTO Branch (branch_name, city, address, phone)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, branch.getBranchName());
            ps.setString(2, branch.getCity());
            ps.setString(3, branch.getAddress());
            ps.setString(4, branch.getPhone());

            return ps.executeUpdate() > 0;

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {

            if (e.getMessage().contains("unique_branch_phone")) {
                System.out.println("Phone number already exists.");
            } else {
                System.out.println("Duplicate or constraint error.");
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error adding branch: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBranch(Branch branch) {
        String sql = """
                UPDATE Branch
                SET branch_name = ?,
                    city = ?,
                    address = ?,
                    phone = ?
                WHERE branch_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, branch.getBranchName());
            ps.setString(2, branch.getCity());
            ps.setString(3, branch.getAddress());
            ps.setString(4, branch.getPhone());
            ps.setInt(5, branch.getBranchId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean branchExists(int branchId) {
        String sql = """
                SELECT branch_id
                FROM Branch
                WHERE branch_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean phoneExists(String phone) {
        return phoneExistsForOtherBranch(phone, 0);
    }

    public static boolean phoneExistsForOtherBranch(String phone, int branchId) {
        String sql = """
                SELECT branch_id
                FROM Branch
                WHERE phone = ?
                  AND branch_id <> ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setInt(2, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
