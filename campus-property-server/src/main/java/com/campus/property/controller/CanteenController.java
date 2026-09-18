package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.*;
import com.campus.property.mapper.UserMapper;
import com.campus.property.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/canteen")
public class CanteenController {

    @Autowired
    private CanteenService canteenService;

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
    public Result<PageResult<Canteen>> listCanteens(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<Canteen> page = canteenService.listCanteens(current, size, keyword);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addCanteen(@RequestBody Canteen canteen) {
        canteenService.addCanteen(canteen);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateCanteen(@RequestBody Canteen canteen) {
        canteenService.updateCanteen(canteen);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCanteen(@PathVariable Long id) {
        canteenService.deleteCanteen(id);
        return Result.success();
    }

    @GetMapping("/dish/list")
    public Result<PageResult<CanteenDish>> listDishes(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long canteenId,
            @RequestParam(required = false) String category) {
        IPage<CanteenDish> page = canteenService.listDishes(current, size, canteenId, category);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/dish")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addDish(@RequestBody CanteenDish dish) {
        canteenService.addDish(dish);
        return Result.success();
    }

    @PutMapping("/dish")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateDish(@RequestBody CanteenDish dish) {
        canteenService.updateDish(dish);
        return Result.success();
    }

    @DeleteMapping("/dish/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> deleteDish(@PathVariable Long id) {
        canteenService.deleteDish(id);
        return Result.success();
    }

    @GetMapping("/consume/list")
    public Result<PageResult<CanteenConsume>> listConsumes(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long canteenId) {
        IPage<CanteenConsume> page = canteenService.listConsumes(current, size, userId, canteenId);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/consume")
    public Result<Void> addConsume(@RequestBody CanteenConsume consume) {
        canteenService.addConsume(consume);
        return Result.success();
    }

    @GetMapping("/inspection/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<CanteenInspection>> listInspections(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long canteenId) {
        IPage<CanteenInspection> page = canteenService.listInspections(current, size, canteenId);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/inspection")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addInspection(@RequestBody CanteenInspection inspection) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getCurrentUserId(auth);
        if (userId != null) {
            inspection.setInspectorId(userId);
        }
        canteenService.addInspection(inspection);
        return Result.success();
    }
}
