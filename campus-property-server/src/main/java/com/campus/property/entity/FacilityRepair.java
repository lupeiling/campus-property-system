package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("facility_repair")
public class FacilityRepair {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long facilityId;

    private Long userId;

    private String title;

    private String description;

    private String images;

    private Integer urgency;

    private Integer status;

    private String repairPerson;

    private String repairResult;

    private LocalDateTime repairTime;

    private Integer evaluated;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
