package Connection;

import ManagerView.EmployeeManagement.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class EmployeeDAO {

    public static ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> list = new ArrayList<>();

        String sql = """
                SELECT employee_id, full_name, position, salary, phone, hire_date, branch_id
                FROM Employee
                ORDER BY employee_id
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("position"),
                        rs.getDouble("salary"),
                        rs.getString("phone"),
                        rs.getDate("hire_date") == null ? null : rs.getDate("hire_date").toLocalDate(),
                        rs.getInt("branch_id")
                );

                list.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertEmployee(String username, String password, Employee employee) {
        String insertUserSql = """
                INSERT INTO users (username, password, role, full_name)
                VALUES (?, ?, 'EMPLOYEE', ?)
                """;

        String insertEmployeeSql = """
                INSERT INTO Employee
                (user_id, full_name, position, salary, phone, hire_date, branch_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement userPs = con.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement employeePs = con.prepareStatement(insertEmployeeSql)
            ) {
                userPs.setString(1, username);
                userPs.setString(2, password);
                userPs.setString(3, employee.getFullName());

                int userAffected = userPs.executeUpdate();

                if (userAffected == 0) {
                    con.rollback();
                    return false;
                }

                int userId;

                try (ResultSet generatedKeys = userPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        con.rollback();
                        return false;
                    }
                }

                employeePs.setInt(1, userId);
                employeePs.setString(2, employee.getFullName());
                employeePs.setString(3, employee.getPosition());
                employeePs.setDouble(4, employee.getSalary());
                employeePs.setString(5, employee.getPhone());

                if (employee.getHireDate() == null) {
                    employeePs.setDate(6, null);
                } else {
                    employeePs.setDate(6, java.sql.Date.valueOf(employee.getHireDate()));
                }

                employeePs.setInt(7, employee.getBranchId());

                int employeeAffected = employeePs.executeUpdate();

                if (employeeAffected == 0) {
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

    public static boolean updateEmployee(Employee employee) {
        String sql = """
                UPDATE Employee
                SET full_name = ?,
                    position = ?,
                    salary = ?,
                    phone = ?,
                    hire_date = ?,
                    branch_id = ?
                WHERE employee_id = ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPosition());
            ps.setDouble(3, employee.getSalary());
            ps.setString(4, employee.getPhone());

            if (employee.getHireDate() == null) {
                ps.setDate(5, null);
            } else {
                ps.setDate(5, java.sql.Date.valueOf(employee.getHireDate()));
            }

            ps.setInt(6, employee.getBranchId());
            ps.setInt(7, employee.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE employee_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}