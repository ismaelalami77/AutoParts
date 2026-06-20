package ManagerView.InventoryManagement;

import java.time.LocalDate;

public class InventoryItem {
    private int placeId;
    private String placeName;
    private int productId;
    private String productName;
    private String categoryName;
    private String brand;
    private int quantity;
    private int reorderLevel;
    private LocalDate lastUpdated;

    public InventoryItem(int placeId, String placeName, int productId, String productName,
                         String categoryName, String brand, int quantity, int reorderLevel,
                         LocalDate lastUpdated) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.brand = brand;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.lastUpdated = lastUpdated;
    }

    public int getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public String getStockStatus() {
        if (quantity <= 0) {
            return "Out of stock";
        }
        if (quantity <= reorderLevel) {
            return "Low stock";
        }
        return "In stock";
    }
}
