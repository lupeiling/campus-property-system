package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dorm_schedule")
public class DormSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String managerName;

    private Long buildingId;

    private LocalDate scheduleDate;

    private String shift;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
