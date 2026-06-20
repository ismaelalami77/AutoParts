package ManagerView.BranchManagement;

public class Branch {

    private int branchId;
    private String branchName;
    private String city;
    private String address;
    private String phone;

    public Branch(int branchId, String branchName, String city, String address, String phone) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.city = city;
        this.address = address;
        this.phone = phone;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return branchName;
    }
}
