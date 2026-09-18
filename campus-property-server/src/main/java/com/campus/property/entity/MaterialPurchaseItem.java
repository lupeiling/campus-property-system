package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("material_purchase_item")
public class MaterialPurchaseItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long purchaseId;

    private Long materialId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
