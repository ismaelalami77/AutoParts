package ManagerView.TransferManagement;

import java.time.LocalDate;

public class TransferRecord {
    private int transferId;
    private LocalDate transferDate;
    private String status;
    private String warehouseName;
    private String branchName;
    private int itemCount;

    public TransferRecord(int transferId, LocalDate transferDate, String status,
                          String warehouseName, String branchName, int itemCount) {
        this.transferId = transferId;
        this.transferDate = transferDate;
        this.status = status;
        this.warehouseName = warehouseName;
        this.branchName = branchName;
        this.itemCount = itemCount;
    }

    public int getTransferId() {
        return transferId;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public String getStatus() {
        return status;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public String getBranchName() {
        return branchName;
    }

    public int getItemCount() {
        return itemCount;
    }
}
