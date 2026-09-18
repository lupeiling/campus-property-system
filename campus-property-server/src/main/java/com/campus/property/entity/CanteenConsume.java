package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("canteen_consume")
public class CanteenConsume {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long canteenId;

    private Long dishId;

    private BigDecimal amount;

    private LocalDateTime consumeTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
