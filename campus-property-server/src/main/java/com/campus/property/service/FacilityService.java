package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.entity.Facility;
import com.campus.property.entity.FacilityRepair;
import com.campus.property.mapper.FacilityMapper;
import com.campus.property.mapper.FacilityRepairMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FacilityService {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private FacilityRepairMapper repairMapper;

    public IPage<Facility> listFacilities(int current, int size, String keyword, String category) {
        Page<Facility> page = new Page<>(current, size);
        LambdaQueryWrapper<Facility> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Facility::getFacilityName, keyword)
                    .or().like(Facility::getFacilityNo, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Facility::getCategory, category);
        }
        wrapper.orderByDesc(Facility::getCreateTime);
        return facilityMapper.selectPage(page, wrapper);
    }

    public void addFacility(Facility facility) {
        facilityMapper.insert(facility);
    }

    public void updateFacility(Facility facility) {
        facilityMapper.updateById(facility);
    }

    public void deleteFacility(Long id) {
        facilityMapper.deleteById(id);
    }

    public IPage<FacilityRepair> listRepairs(int current, int size, Long facilityId, Integer status, Long userId, String statusList, String repairPerson) {
        Page<FacilityRepair> page = new Page<>(current, size);
        LambdaQueryWrapper<FacilityRepair> wrapper = new LambdaQueryWrapper<>();
        if (facilityId != null) {
            wrapper.eq(FacilityRepair::getFacilityId, facilityId);
        }
        if (status != null) {
            wrapper.eq(FacilityRepair::getStatus, status);
        } else if (statusList != null && !statusList.isEmpty()) {
            String[] parts = statusList.split(",");
            Integer[] statuses = new Integer[parts.length];
            for (int i = 0; i < parts.length; i++) {
                statuses[i] = Integer.valueOf(parts[i].trim());
            }
            wrapper.in(FacilityRepair::getStatus, (Object[]) statuses);
        }
        if (userId != null) {
            wrapper.eq(FacilityRepair::getUserId, userId);
        }
        if (repairPerson != null && !repairPerson.isEmpty()) {
            wrapper.eq(FacilityRepair::getRepairPerson, repairPerson);
        }
        wrapper.orderByDesc(FacilityRepair::getCreateTime);
        return repairMapper.selectPage(page, wrapper);
    }

    public void addRepair(FacilityRepair repair) {
        repair.setStatus(0);
        repairMapper.insert(repair);
        Facility facility = facilityMapper.selectById(repair.getFacilityId());
        if (facility != null && facility.getStatus() != 2) {
            facility.setStatus(2);
            facilityMapper.updateById(facility);
        }
    }

    public void handleRepair(Long id, Integer status, String repairPerson, String repairResult) {
        FacilityRepair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }
        repair.setStatus(status);
        repair.setRepairPerson(repairPerson);
        repair.setRepairResult(repairResult);
        if (status == 2) {
            repair.setRepairTime(LocalDateTime.now());
        }
        repairMapper.updateById(repair);

        if (status == 2 || status == 3) {
            LambdaQueryWrapper<FacilityRepair> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FacilityRepair::getFacilityId, repair.getFacilityId());
            wrapper.in(FacilityRepair::getStatus, 0, 1);
            Long pendingCount = repairMapper.selectCount(wrapper);
            if (pendingCount == 0) {
                Facility facility = facilityMapper.selectById(repair.getFacilityId());
                if (facility != null) {
                    facility.setStatus(1);
                    facilityMapper.updateById(facility);
                }
            }
        }
    }

    public void evaluateRepair(Long id) {
        FacilityRepair repair = repairMapper.selectById(id);
        if (repair != null) {
            repair.setEvaluated(1);
            repairMapper.updateById(repair);
        }
    }
}
