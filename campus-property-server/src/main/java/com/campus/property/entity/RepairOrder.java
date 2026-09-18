package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair_order")
public class RepairOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String title;

    private String description;

    private String images;

    private Integer sourceType;

    private Long sourceId;

    private Long applicantId;

    private String category;

    private Integer urgency;

    private Integer status;

    private Long repairPersonId;

    private LocalDateTime assignTime;

    private LocalDateTime startTime;

    private LocalDateTime completeTime;

    private String repairResult;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
