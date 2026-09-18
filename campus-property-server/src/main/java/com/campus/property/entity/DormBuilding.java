package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_building")
public class DormBuilding {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String buildingName;

    private String buildingNo;

    private Integer floors;

    private Integer roomsPerFloor;

    private Integer capacity;

    private Integer genderType;

    private Long managerId;

    private String description;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
