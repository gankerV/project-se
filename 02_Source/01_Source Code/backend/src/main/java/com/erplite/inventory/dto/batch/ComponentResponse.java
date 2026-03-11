package com.erplite.inventory.dto.batch;

import com.erplite.inventory.entity.BatchComponent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ComponentResponse {

    private String componentId;
    private String lotId;
    private String materialName;
    private String partNumber;
    private String lotStatus;
    private BigDecimal lotAvailableQuantity;
    private BigDecimal plannedQuantity;
    private BigDecimal actualQuantity;
    private String unitOfMeasure;
    private LocalDateTime additionDate;
    private String addedBy;

    public static ComponentResponse from(BatchComponent c) {
        ComponentResponse dto = new ComponentResponse();
        dto.setComponentId(c.getComponentId());
        dto.setPlannedQuantity(c.getPlannedQuantity());
        dto.setActualQuantity(c.getActualQuantity());
        dto.setUnitOfMeasure(c.getUnitOfMeasure());
        dto.setAdditionDate(c.getAdditionDate());
        dto.setAddedBy(c.getAddedBy());
        if (c.getLot() != null) {
            dto.setLotId(c.getLot().getLotId());
            dto.setLotStatus(c.getLot().getStatus() != null ? c.getLot().getStatus().name() : null);
            dto.setLotAvailableQuantity(c.getLot().getQuantity());
            if (c.getLot().getMaterial() != null) {
                dto.setMaterialName(c.getLot().getMaterial().getMaterialName());
                dto.setPartNumber(c.getLot().getMaterial().getPartNumber());
            }
        }
        return dto;
    }
}
