package com.erplite.inventory.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class InventorySnapshotResponse {
    private String materialId;
    private String partNumber;
    private String materialName;
    private String materialType;
    private Map<String, LotCountQty> lots;
    private BigDecimal totalAvailable;
    private String unit;

    @Data
    @Builder
    public static class LotCountQty {
        private int count;
        private BigDecimal totalQuantity;
    }
}
