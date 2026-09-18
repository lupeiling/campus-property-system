package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_fee")
public class StudentFee {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String feeType;

    private BigDecimal amount;

    private String semester;

    private Integer status;

    private java.time.LocalDate dueDate;

    private LocalDateTime payTime;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
