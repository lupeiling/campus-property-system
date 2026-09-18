package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dorm_allocation")
public class DormAllocation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roomId;

    private Integer bedNo;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
