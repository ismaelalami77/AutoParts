package EmployeeView;

import java.time.LocalDateTime;

public class ReceiptSale {
    private int orderId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String paymentMethod;

    public ReceiptSale(int orderId, String customerName, String customerPhone,
                       LocalDateTime orderDate, double totalAmount, String paymentMethod) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
