package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.dto.PurchaseApplyDTO;
import com.campus.property.entity.*;
import com.campus.property.mapper.UserMapper;
import com.campus.property.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public Result<PageResult<Material>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        IPage<Material> page = materialService.listMaterials(current, size, keyword, categoryId);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> add(@RequestBody Material material) {
        materialService.addMaterial(material);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> update(@RequestBody Material material) {
        materialService.updateMaterial(material);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return Result.success();
    }

    @GetMapping("/category/list")
    public Result<List<MaterialCategory>> listCategories() {
        return Result.success(materialService.listCategories());
    }

    @PostMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addCategory(@RequestBody MaterialCategory category) {
        materialService.addCategory(category);
        return Result.success();
    }

    @PostMapping("/purchase/apply")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> applyPurchase(@RequestBody PurchaseApplyDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByUsername(auth.getName());
        materialService.applyPurchase(dto, user.getId());
        return Result.success();
    }

    @GetMapping("/purchase/list")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<PageResult<MaterialPurchase>> listPurchases(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        IPage<MaterialPurchase> page = materialService.listPurchases(current, size, status);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/purchase/{id}/items")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<List<MaterialPurchaseItem>> getPurchaseItems(@PathVariable Long id) {
        return Result.success(materialService.getPurchaseItems(id));
    }

    @PutMapping("/purchase/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> approvePurchase(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String remark = (String) params.get("remark");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByUsername(auth.getName());
        materialService.approvePurchase(id, user.getId(), status, remark);
        return Result.success();
    }

    @GetMapping("/usage/list")
    public Result<PageResult<MaterialUsage>> listUsages(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long userId) {
        IPage<MaterialUsage> page = materialService.listUsages(current, size, materialId, userId);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/usage")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> useMaterial(@RequestBody Map<String, Object> params) {
        Long materialId = Long.valueOf(params.get("materialId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        String purpose = (String) params.get("purpose");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByUsername(auth.getName());
        materialService.useMaterial(materialId, user.getId(), quantity, purpose);
        return Result.success();
    }

    @PutMapping("/usage/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> returnMaterial(@PathVariable Long id) {
        materialService.returnMaterial(id);
        return Result.success();
    }

    private User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
