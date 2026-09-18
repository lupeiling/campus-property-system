package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.StudentFee;
import com.campus.property.entity.User;
import com.campus.property.mapper.StudentFeeMapper;
import com.campus.property.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
public class StudentFeeController {

    @Autowired
    private StudentFeeMapper studentFeeMapper;

    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, auth.getName());
        User user = userMapper.selectOne(wrapper);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/list")
    public Result<PageResult<StudentFee>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String feeType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

        LambdaQueryWrapper<StudentFee> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && !isStaff) {
            Long userId = getCurrentUserId();
            if (userId != null) wrapper.eq(StudentFee::getUserId, userId);
        }
        if (status != null) wrapper.eq(StudentFee::getStatus, status);
        if (semester != null && !semester.isEmpty()) wrapper.eq(StudentFee::getSemester, semester);
        if (feeType != null && !feeType.isEmpty()) wrapper.eq(StudentFee::getFeeType, feeType);
        wrapper.orderByDesc(StudentFee::getCreateTime);

        IPage<StudentFee> page = studentFeeMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    public Result<Void> add(@RequestBody StudentFee fee) {
        studentFeeMapper.insert(fee);
        return Result.success();
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        StudentFee fee = studentFeeMapper.selectById(id);
        if (fee == null) return Result.error("记录不存在");
        if (fee.getStatus() == 1) return Result.error("已缴费");
        fee.setStatus(1);
        fee.setPayTime(LocalDateTime.now());
        studentFeeMapper.updateById(fee);
        return Result.success();
    }

    @GetMapping("/semesters")
    public Result<java.util.List<String>> getSemesters() {
        return Result.success(java.util.Arrays.asList("2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2"));
    }
}
