package Connection;

import ManagerView.EmployeeManagement.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        String insertEmployeeSql = """
                INSERT INTO Employee
                (username, password, full_name, position, salary, phone, hire_date, branch_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement employeePs = con.prepareStatement(insertEmployeeSql)) {

                employeePs.setString(1, username);
                employeePs.setString(2, password);
                employeePs.setString(3, employee.getFullName());
                employeePs.setString(4, employee.getPosition());
                employeePs.setDouble(5, employee.getSalary());
                employeePs.setString(6, employee.getPhone());

                if (employee.getHireDate() == null) {
                    employeePs.setDate(7, null);
                } else {
                    employeePs.setDate(7, java.sql.Date.valueOf(employee.getHireDate()));
                }

                employeePs.setInt(8, employee.getBranchId());

                return employeePs.executeUpdate() > 0;

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

    public static boolean phoneExists(String phone) {
        return phoneExistsForOtherEmployee(phone, 0);
    }

    public static boolean phoneExistsForOtherEmployee(String phone, int employeeId) {
        String sql = """
                SELECT employee_id
                FROM Employee
                WHERE phone = ?
                  AND employee_id <> ?
                """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setInt(2, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
