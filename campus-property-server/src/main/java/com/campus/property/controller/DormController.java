package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.*;
import com.campus.property.mapper.UserMapper;
import com.campus.property.service.DormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dorm")
public class DormController {

    @Autowired
    private DormService dormService;

    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, auth.getName());
        User user = userMapper.selectOne(wrapper);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/building/list")
    public Result<PageResult<DormBuilding>> listBuildings(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<DormBuilding> page = dormService.listBuildings(current, size, keyword);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/building")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addBuilding(@RequestBody DormBuilding building) {
        dormService.addBuilding(building);
        return Result.success();
    }

    @PutMapping("/building")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateBuilding(@RequestBody DormBuilding building) {
        dormService.updateBuilding(building);
        return Result.success();
    }

    @DeleteMapping("/building/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBuilding(@PathVariable Long id) {
        dormService.deleteBuilding(id);
        return Result.success();
    }

    @GetMapping("/room/list")
    public Result<PageResult<DormRoom>> listRooms(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long buildingId) {
        IPage<DormRoom> page = dormService.listRooms(current, size, buildingId);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> addRoom(@RequestBody DormRoom room) {
        dormService.addRoom(room);
        return Result.success();
    }

    @PutMapping("/room")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> updateRoom(@RequestBody DormRoom room) {
        dormService.updateRoom(room);
        return Result.success();
    }

    @DeleteMapping("/room/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        dormService.deleteRoom(id);
        return Result.success();
    }

    @PostMapping("/allocation")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> allocateRoom(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long roomId = Long.valueOf(params.get("roomId").toString());
        Integer bedNo = Integer.valueOf(params.get("bedNo").toString());
        dormService.allocateRoom(userId, roomId, bedNo);
        return Result.success();
    }

    @PutMapping("/allocation/{id}/checkout")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> checkOut(@PathVariable Long id) {
        dormService.checkOut(id);
        return Result.success();
    }

    @GetMapping("/allocation/list")
    public Result<PageResult<DormAllocation>> listAllocations(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long userId) {
        IPage<DormAllocation> page = dormService.listAllocations(current, size, roomId, userId);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/repair/list")
    public Result<PageResult<DormRepair>> listRepairs(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long roomId,
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
        IPage<DormRepair> page = dormService.listRepairs(current, size, roomId, status, userId, statusList, repairPerson);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping("/repair")
    public Result<Void> addRepair(@RequestBody DormRepair repair) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getCurrentUserId(auth);
        if (userId != null) {
            repair.setUserId(userId);
        }
        dormService.addRepair(repair);
        return Result.success();
    }

    @PutMapping("/repair/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> handleRepair(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String repairPerson = (String) params.get("repairPerson");
        String repairResult = (String) params.get("repairResult");
        dormService.handleRepair(id, status, repairPerson, repairResult);
        return Result.success();
    }

    @PutMapping("/repair/{id}/evaluate")
    public Result<Void> evaluateRepair(@PathVariable Long id) {
        dormService.evaluateRepair(id);
        return Result.success();
    }
}
