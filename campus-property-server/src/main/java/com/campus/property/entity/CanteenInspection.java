package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("canteen_inspection")
public class CanteenInspection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long canteenId;

    private Long inspectorId;

    private LocalDate inspectDate;

    private Integer score;

    private Integer hygieneStatus;

    private String issues;

    private String rectification;

    private String images;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
