package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.property.common.Result;
import com.campus.property.entity.*;
import com.campus.property.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DormBuildingMapper dormBuildingMapper;
    @Autowired
    private DormRoomMapper dormRoomMapper;
    @Autowired
    private DormAllocationMapper dormAllocationMapper;
    @Autowired
    private DormRepairMapper dormRepairMapper;
    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private FacilityRepairMapper facilityRepairMapper;
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private MaterialPurchaseMapper materialPurchaseMapper;
    @Autowired
    private MaterialUsageMapper materialUsageMapper;
    @Autowired
    private CanteenMapper canteenMapper;
    @Autowired
    private CanteenInspectionMapper canteenInspectionMapper;
    @Autowired
    private CanteenConsumeMapper canteenConsumeMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();

        data.put("userCount", userMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("buildingCount", dormBuildingMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("roomCount", dormRoomMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("allocationCount", dormAllocationMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("facilityCount", facilityMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("materialCount", materialMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("canteenCount", canteenMapper.selectCount(new LambdaQueryWrapper<>()));

        Long pendingDormRepair = dormRepairMapper.selectCount(new LambdaQueryWrapper<DormRepair>().eq(DormRepair::getStatus, 0));
        Long pendingFacilityRepair = facilityRepairMapper.selectCount(new LambdaQueryWrapper<FacilityRepair>().eq(FacilityRepair::getStatus, 0));
        data.put("pendingRepairCount", pendingDormRepair + pendingFacilityRepair);

        Long totalDormRepair = dormRepairMapper.selectCount(new LambdaQueryWrapper<>());
        Long totalFacilityRepair = facilityRepairMapper.selectCount(new LambdaQueryWrapper<>());
        data.put("totalRepairCount", totalDormRepair + totalFacilityRepair);

        return Result.success(data);
    }

    @GetMapping("/repair-status")
    public Result<Map<String, Object>> getRepairStatus() {
        Map<String, Object> data = new HashMap<>();

        List<Map<String, Object>> dormStatus = new ArrayList<>();
        String[] statusNames = {"待处理", "处理中", "已完成", "已拒绝"};
        for (int i = 0; i < 4; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i]);
            item.put("dormRepair", dormRepairMapper.selectCount(new LambdaQueryWrapper<DormRepair>().eq(DormRepair::getStatus, i)));
            item.put("facilityRepair", facilityRepairMapper.selectCount(new LambdaQueryWrapper<FacilityRepair>().eq(FacilityRepair::getStatus, i)));
            dormStatus.add(item);
        }
        data.put("statusList", dormStatus);

        return Result.success(data);
    }

    @GetMapping("/repair-monthly")
    public Result<List<Map<String, Object>>> getRepairMonthly() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(11).withDayOfMonth(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDate monthStart = start.plusMonths(i);
            LocalDate monthEnd = monthStart.plusMonths(1);
            String monthLabel = monthStart.format(fmt);

            Map<String, Object> item = new HashMap<>();
            item.put("month", monthLabel);
            item.put("dormRepair", dormRepairMapper.selectCount(new QueryWrapper<DormRepair>()
                    .ge("create_time", monthStart.atStartOfDay())
                    .lt("create_time", monthEnd.atStartOfDay())));
            item.put("facilityRepair", facilityRepairMapper.selectCount(new QueryWrapper<FacilityRepair>()
                    .ge("create_time", monthStart.atStartOfDay())
                    .lt("create_time", monthEnd.atStartOfDay())));
            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/repair-trend")
    public Result<List<Map<String, Object>>> getRepairTrend() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(29);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate day = start.plusDays(i);
            LocalDate nextDay = day.plusDays(1);
            String dayLabel = day.format(fmt);

            Map<String, Object> item = new HashMap<>();
            item.put("date", dayLabel);
            item.put("count", dormRepairMapper.selectCount(new QueryWrapper<DormRepair>()
                    .ge("create_time", day.atStartOfDay())
                    .lt("create_time", nextDay.atStartOfDay()))
                    + facilityRepairMapper.selectCount(new QueryWrapper<FacilityRepair>()
                    .ge("create_time", day.atStartOfDay())
                    .lt("create_time", nextDay.atStartOfDay())));
            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/category-distribution")
    public Result<Map<String, Object>> getCategoryDistribution() {
        Map<String, Object> data = new HashMap<>();

        data.put("dormRepairTotal", dormRepairMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("facilityRepairTotal", facilityRepairMapper.selectCount(new LambdaQueryWrapper<>()));

        List<Map<String, Object>> userRoles = new ArrayList<>();
        String[] roleNames = {"ADMIN", "STAFF", "TEACHER", "STUDENT"};
        String[] roleLabels = {"管理员", "后勤人员", "教师", "学生"};
        for (int i = 0; i < roleNames.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", roleLabels[i]);
            item.put("value", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, roleNames[i])));
            userRoles.add(item);
        }
        data.put("userRoles", userRoles);

        return Result.success(data);
    }

    @GetMapping("/facility-status")
    public Result<List<Map<String, Object>>> getFacilityStatus() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] statusNames = {"不可用", "正常", "维修中"};
        for (int i = 0; i < 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i]);
            item.put("value", facilityMapper.selectCount(new LambdaQueryWrapper<Facility>().eq(Facility::getStatus, i)));
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/material-overview")
    public Result<Map<String, Object>> getMaterialOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalTypes", materialMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("purchaseCount", materialPurchaseMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("usageCount", materialUsageMapper.selectCount(new LambdaQueryWrapper<>()));

        List<Material> materials = materialMapper.selectList(new LambdaQueryWrapper<Material>()
                .orderByDesc(Material::getStockQuantity).last("LIMIT 5"));
        data.put("topMaterials", materials.stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", m.getMaterialName());
            item.put("quantity", m.getStockQuantity());
            item.put("unit", m.getUnit());
            return item;
        }).collect(Collectors.toList()));

        return Result.success(data);
    }
}
