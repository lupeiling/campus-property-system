package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cleaning_assignment")
public class CleaningAssignment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cleanerName;

    private Long buildingId;

    private LocalDate weekStart;

    private LocalDate weekEnd;

    private Integer status;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
