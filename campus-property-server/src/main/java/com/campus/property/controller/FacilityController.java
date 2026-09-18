package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.Facility;
import com.campus.property.entity.FacilityRepair;
import com.campus.property.entity.User;
import com.campus.property.mapper.UserMapper;
import com.campus.property.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

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
    public Result<PageResult<Facility>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        IPage<Facility> page = facilityService.listFacilities(current, size, keyword, category);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> add(@RequestBody Facility facility) {
        facilityService.addFacility(facility);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> update(@RequestBody Facility facility) {
        facilityService.updateFacility(facility);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return Result.success();
    }

    @GetMapping("/repair/list")
    public Result<PageResult<FacilityRepair>> listRepairs(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String statusList,
            @RequestParam(required = false) String repairPerson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))
                && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, auth.getName());
            User currentUser = userMapper.selectOne(wrapper);
            if (currentUser != null) {
                repairPerson = currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUsername();
            }
        }
        IPage<FacilityRepair> page = facilityService.listRepairs(current, size, facilityId, status, userId, statusList, repairPerson);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/repair")
    public Result<Void> addRepair(@RequestBody FacilityRepair repair) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getCurrentUserId(auth);
        if (userId != null) {
            repair.setUserId(userId);
        }
        facilityService.addRepair(repair);
        return Result.success();
    }

    @PutMapping("/repair/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> handleRepair(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String repairPerson = (String) params.get("repairPerson");
        String repairResult = (String) params.get("repairResult");
        facilityService.handleRepair(id, status, repairPerson, repairResult);
        return Result.success();
    }

    @PutMapping("/repair/{id}/evaluate")
    public Result<Void> evaluateRepair(@PathVariable Long id) {
        facilityService.evaluateRepair(id);
        return Result.success();
    }
}
