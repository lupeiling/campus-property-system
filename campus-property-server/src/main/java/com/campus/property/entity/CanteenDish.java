package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("canteen_dish")
public class CanteenDish {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long canteenId;

    private String dishName;

    private String category;

    private BigDecimal price;

    private String description;

    private String image;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
