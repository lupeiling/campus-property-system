package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_repair")
public class DormRepair {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private Long userId;

    private String title;

    private String description;

    private String images;

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
