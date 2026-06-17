package EmployeeView;

import ManagerView.ProductManagement.Product;

public class CartItem {

    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public CartItem(Product product, int quantity) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.quantity = quantity;
        this.unitPrice = product.getSellingPrice();
        this.subtotal = quantity * unitPrice;
    }

    public void increaseQuantity() {
        quantity++;
        subtotal = quantity * unitPrice;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }
}