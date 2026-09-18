package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("material_purchase")
public class MaterialPurchase {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String purchaseNo;

    private Long applicantId;

    private String title;

    private BigDecimal totalAmount;

    private Integer status;

    private Long approveId;

    private LocalDateTime approveTime;

    private String approveRemark;

    @TableField(exist = false)
    private String applicantName;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
