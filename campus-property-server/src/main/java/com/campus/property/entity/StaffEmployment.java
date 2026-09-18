package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("staff_employment")
public class StaffEmployment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String position;

    private String phone;

    private BigDecimal salary;

    private LocalDate hireDate;

    private Integer status;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
