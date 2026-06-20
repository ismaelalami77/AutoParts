package ManagerView.PurchaseManagement;

import java.time.LocalDate;

public class PurchaseOrder {
    private int purchaseOrderId;
    private LocalDate orderDate;
    private double totalAmount;
    private String status;
    private String supplierName;
    private String warehouseName;
    private int itemCount;

    public PurchaseOrder(int purchaseOrderId, LocalDate orderDate, double totalAmount, String status,
                         String supplierName, String warehouseName, int itemCount) {
        this.purchaseOrderId = purchaseOrderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.supplierName = supplierName;
        this.warehouseName = warehouseName;
        this.itemCount = itemCount;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public int getItemCount() {
        return itemCount;
    }
}
