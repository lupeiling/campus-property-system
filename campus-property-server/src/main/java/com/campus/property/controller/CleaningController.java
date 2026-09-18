package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.CleaningAssignment;
import com.campus.property.entity.CleaningRecord;
import com.campus.property.mapper.CleaningAssignmentMapper;
import com.campus.property.mapper.CleaningRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cleaning")
public class CleaningController {

    @Autowired
    private CleaningAssignmentMapper cleaningAssignmentMapper;

    @Autowired
    private CleaningRecordMapper cleaningRecordMapper;

    @GetMapping("/assignment/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<CleaningAssignment>> listAssignments(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<CleaningAssignment> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) wrapper.eq(CleaningAssignment::getBuildingId, buildingId);
        wrapper.orderByDesc(CleaningAssignment::getWeekStart);
        IPage<CleaningAssignment> page = cleaningAssignmentMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/assignment")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addAssignment(@RequestBody CleaningAssignment assignment) {
        cleaningAssignmentMapper.insert(assignment);
        return Result.success();
    }

    @PutMapping("/assignment")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateAssignment(@RequestBody CleaningAssignment assignment) {
        cleaningAssignmentMapper.updateById(assignment);
        return Result.success();
    }

    @DeleteMapping("/assignment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> deleteAssignment(@PathVariable Long id) {
        cleaningAssignmentMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/record/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<CleaningRecord>> listRecords(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long assignmentId,
            @RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<CleaningRecord> wrapper = new LambdaQueryWrapper<>();
        if (assignmentId != null) wrapper.eq(CleaningRecord::getAssignmentId, assignmentId);
        if (buildingId != null) wrapper.eq(CleaningRecord::getBuildingId, buildingId);
        wrapper.orderByDesc(CleaningRecord::getCleanDate);
        IPage<CleaningRecord> page = cleaningRecordMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/record")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addRecord(@RequestBody CleaningRecord record) {
        cleaningRecordMapper.insert(record);
        return Result.success();
    }

    @PutMapping("/record")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateRecord(@RequestBody CleaningRecord record) {
        cleaningRecordMapper.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/record/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        cleaningRecordMapper.deleteById(id);
        return Result.success();
    }
}
