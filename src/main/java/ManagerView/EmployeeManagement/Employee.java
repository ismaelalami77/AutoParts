package ManagerView.EmployeeManagement;

import java.time.LocalDate;

public class Employee {

    private int employeeId;
    private String fullName;
    private String position;
    private double salary;
    private String phone;
    private LocalDate hireDate;
    private int branchId;

    public Employee(int employeeId, String fullName, String position,
                    double salary, String phone, LocalDate hireDate, int branchId) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.position = position;
        this.salary = salary;
        this.phone = phone;
        this.hireDate = hireDate;
        this.branchId = branchId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}