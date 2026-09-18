package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("teacher_housing")
public class TeacherHousing {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String apartmentName;

    private String location;

    private String titleLevel;

    private BigDecimal rent;

    private BigDecimal propertyFee;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
