package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.DormAudit;
import com.campus.property.mapper.DormAuditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dorm/audit")
public class DormAuditController {

    @Autowired
    private DormAuditMapper dormAuditMapper;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<DormAudit>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String auditType) {
        LambdaQueryWrapper<DormAudit> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(DormAudit::getStatus, status);
        if (auditType != null && !auditType.isEmpty()) wrapper.eq(DormAudit::getAuditType, auditType);
        wrapper.orderByDesc(DormAudit::getCreateTime);
        IPage<DormAudit> page = dormAuditMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    public Result<Void> add(@RequestBody DormAudit audit) {
        dormAuditMapper.insert(audit);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        DormAudit audit = dormAuditMapper.selectById(id);
        if (audit == null) return Result.error("记录不存在");
        audit.setStatus(1);
        audit.setAuditBy((String) params.get("auditBy"));
        audit.setAuditTime(LocalDateTime.now());
        audit.setAuditRemark((String) params.get("auditRemark"));
        dormAuditMapper.updateById(audit);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        DormAudit audit = dormAuditMapper.selectById(id);
        if (audit == null) return Result.error("记录不存在");
        audit.setStatus(2);
        audit.setAuditBy((String) params.get("auditBy"));
        audit.setAuditTime(LocalDateTime.now());
        audit.setAuditRemark((String) params.get("auditRemark"));
        dormAuditMapper.updateById(audit);
        return Result.success();
    }
}
