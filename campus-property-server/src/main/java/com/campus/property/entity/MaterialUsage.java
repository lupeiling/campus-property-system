package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material_usage")
public class MaterialUsage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private Long userId;

    private Integer quantity;

    private String purpose;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
