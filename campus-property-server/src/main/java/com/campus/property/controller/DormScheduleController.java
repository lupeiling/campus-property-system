package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.DormSchedule;
import com.campus.property.mapper.DormScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dorm/schedule")
public class DormScheduleController {

    @Autowired
    private DormScheduleMapper dormScheduleMapper;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<DormSchedule>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<DormSchedule> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) wrapper.eq(DormSchedule::getBuildingId, buildingId);
        wrapper.orderByDesc(DormSchedule::getScheduleDate);
        IPage<DormSchedule> page = dormScheduleMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> add(@RequestBody DormSchedule schedule) {
        dormScheduleMapper.insert(schedule);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> update(@RequestBody DormSchedule schedule) {
        dormScheduleMapper.updateById(schedule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        dormScheduleMapper.deleteById(id);
        return Result.success();
    }
}
