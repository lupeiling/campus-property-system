package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_room")
public class DormRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long buildingId;

    private String roomNo;

    private Integer floor;

    private Integer capacity;

    private String roomType;

    private Integer currentCount;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
