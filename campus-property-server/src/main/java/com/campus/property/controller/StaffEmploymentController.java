package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.StaffEmployment;
import com.campus.property.mapper.StaffEmploymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/employment")
public class StaffEmploymentController {

    @Autowired
    private StaffEmploymentMapper staffEmploymentMapper;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<StaffEmployment>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String position) {
        LambdaQueryWrapper<StaffEmployment> wrapper = new LambdaQueryWrapper<>();
        if (position != null && !position.isEmpty()) wrapper.eq(StaffEmployment::getPosition, position);
        wrapper.orderByDesc(StaffEmployment::getCreateTime);
        IPage<StaffEmployment> page = staffEmploymentMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> add(@RequestBody StaffEmployment employment) {
        staffEmploymentMapper.insert(employment);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> update(@RequestBody StaffEmployment employment) {
        staffEmploymentMapper.updateById(employment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        staffEmploymentMapper.deleteById(id);
        return Result.success();
    }
}
