package com.campus.property.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseApplyDTO {

    private String title;
    private List<PurchaseItemDTO> items;

    @Data
    public static class PurchaseItemDTO {
        private Long materialId;
        private Integer quantity;
        private java.math.BigDecimal unitPrice;
        private String remark;
    }
}
