package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("facility")
public class Facility {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String facilityName;

    private String facilityNo;

    private String category;

    private String location;

    private Integer status;

    private String description;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
