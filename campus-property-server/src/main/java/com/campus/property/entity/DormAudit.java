package com.campus.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dorm_audit")
public class DormAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String auditType;

    private Long roomId;

    private Integer bedNo;

    private Long newRoomId;

    private Integer newBedNo;

    private String reason;

    private Integer status;

    private String auditBy;

    private LocalDateTime auditTime;

    private String auditRemark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
