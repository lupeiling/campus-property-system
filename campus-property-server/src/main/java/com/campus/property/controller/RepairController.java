package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.RepairEvaluation;
import com.campus.property.entity.RepairOrder;
import com.campus.property.entity.User;
import com.campus.property.mapper.UserMapper;
import com.campus.property.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, auth.getName());
        User user = userMapper.selectOne(wrapper);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/list")
    public Result<PageResult<RepairOrder>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer sourceType,
            @RequestParam(required = false) Long applicantId) {
        IPage<RepairOrder> page = repairService.listOrders(current, size, status, sourceType, applicantId);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/{id}")
    public Result<RepairOrder> getById(@PathVariable Long id) {
        return Result.success(repairService.getOrderById(id));
    }

    @PostMapping
    public Result<Void> submit(@RequestBody RepairOrder order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getCurrentUserId(auth);
        if (userId != null) {
            order.setApplicantId(userId);
        }
        repairService.submitOrder(order);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        repairService.approveOrder(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> assign(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long repairPersonId = Long.valueOf(params.get("repairPersonId").toString());
        repairService.assignOrder(id, repairPersonId);
        return Result.success();
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> start(@PathVariable Long id) {
        repairService.startRepair(id);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> complete(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String repairResult = (String) params.get("repairResult");
        repairService.completeRepair(id, repairResult);
        return Result.success();
    }

    @PostMapping("/evaluation")
    public Result<Void> addEvaluation(@RequestBody RepairEvaluation evaluation) {
        repairService.addEvaluation(evaluation);
        return Result.success();
    }

    @GetMapping("/evaluation/{orderId}")
    public Result<RepairEvaluation> getEvaluation(@PathVariable Long orderId) {
        return Result.success(repairService.getEvaluationByOrderId(orderId));
    }
}
