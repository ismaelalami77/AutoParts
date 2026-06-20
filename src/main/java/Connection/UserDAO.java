package Connection;

import Login.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User authenticate(String username, String password) {
        String sql = """
                SELECT employee_id, username, full_name, position, branch_id
                FROM Employee
                WHERE username = ?
                  AND password = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String position = rs.getString("position") == null ? "" : rs.getString("position");
                    String role = position.toLowerCase().contains("manager") ? "MANAGER" : "EMPLOYEE";

                    return new User(
                            rs.getInt("employee_id"),
                            rs.getString("username"),
                            role,
                            rs.getString("full_name"),
                            rs.getInt("branch_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
