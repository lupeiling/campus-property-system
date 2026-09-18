package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cleaning_record")
public class CleaningRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private String cleanerName;

    private Long buildingId;

    private LocalDate cleanDate;

    private Integer score;

    private String issues;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
