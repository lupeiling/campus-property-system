package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.entity.DormBuilding;
import com.campus.property.entity.DormRoom;
import com.campus.property.entity.DormAllocation;
import com.campus.property.entity.DormRepair;
import com.campus.property.mapper.DormBuildingMapper;
import com.campus.property.mapper.DormRoomMapper;
import com.campus.property.mapper.DormAllocationMapper;
import com.campus.property.mapper.DormRepairMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DormService {

    @Autowired
    private DormBuildingMapper buildingMapper;

    @Autowired
    private DormRoomMapper roomMapper;

    @Autowired
    private DormAllocationMapper allocationMapper;

    @Autowired
    private DormRepairMapper repairMapper;

    public IPage<DormBuilding> listBuildings(int current, int size, String keyword) {
        Page<DormBuilding> page = new Page<>(current, size);
        LambdaQueryWrapper<DormBuilding> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(DormBuilding::getBuildingName, keyword)
                    .or().like(DormBuilding::getBuildingNo, keyword);
        }
        wrapper.orderByDesc(DormBuilding::getCreateTime);
        return buildingMapper.selectPage(page, wrapper);
    }

    public void addBuilding(DormBuilding building) {
        buildingMapper.insert(building);
    }

    public void updateBuilding(DormBuilding building) {
        buildingMapper.updateById(building);
    }

    public void deleteBuilding(Long id) {
        buildingMapper.deleteById(id);
    }

    public IPage<DormRoom> listRooms(int current, int size, Long buildingId) {
        Page<DormRoom> page = new Page<>(current, size);
        LambdaQueryWrapper<DormRoom> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(DormRoom::getBuildingId, buildingId);
        }
        wrapper.orderByAsc(DormRoom::getRoomNo);
        return roomMapper.selectPage(page, wrapper);
    }

    public void addRoom(DormRoom room) {
        roomMapper.insert(room);
    }

    public void updateRoom(DormRoom room) {
        roomMapper.updateById(room);
    }

    public void deleteRoom(Long id) {
        roomMapper.deleteById(id);
    }

    @Transactional
    public void allocateRoom(Long userId, Long roomId, Integer bedNo) {
        DormRoom room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getCurrentCount() >= room.getCapacity()) {
            throw new RuntimeException("房间已满");
        }

        DormAllocation allocation = new DormAllocation();
        allocation.setUserId(userId);
        allocation.setRoomId(roomId);
        allocation.setBedNo(bedNo);
        allocation.setCheckInDate(LocalDate.now());
        allocation.setStatus(1);
        allocationMapper.insert(allocation);

        room.setCurrentCount(room.getCurrentCount() + 1);
        room.setStatus(room.getCurrentCount() >= room.getCapacity() ? 0 : 1);
        roomMapper.updateById(room);
    }

    @Transactional
    public void checkOut(Long allocationId) {
        DormAllocation allocation = allocationMapper.selectById(allocationId);
        if (allocation == null) {
            throw new RuntimeException("分配记录不存在");
        }
        allocation.setStatus(0);
        allocation.setCheckOutDate(LocalDate.now());
        allocationMapper.updateById(allocation);

        DormRoom room = roomMapper.selectById(allocation.getRoomId());
        if (room != null) {
            room.setCurrentCount(Math.max(0, room.getCurrentCount() - 1));
            room.setStatus(room.getCurrentCount() >= room.getCapacity() ? 0 : 1);
            roomMapper.updateById(room);
        }
    }

    public IPage<DormAllocation> listAllocations(int current, int size, Long roomId, Long userId) {
        Page<DormAllocation> page = new Page<>(current, size);
        LambdaQueryWrapper<DormAllocation> wrapper = new LambdaQueryWrapper<>();
        if (roomId != null) {
            wrapper.eq(DormAllocation::getRoomId, roomId);
        }
        if (userId != null) {
            wrapper.eq(DormAllocation::getUserId, userId);
        }
        wrapper.orderByDesc(DormAllocation::getCreateTime);
        return allocationMapper.selectPage(page, wrapper);
    }

    public IPage<DormRepair> listRepairs(int current, int size, Long roomId, Integer status, Long userId, String statusList, String repairPerson) {
        Page<DormRepair> page = new Page<>(current, size);
        LambdaQueryWrapper<DormRepair> wrapper = new LambdaQueryWrapper<>();
        if (roomId != null) {
            wrapper.eq(DormRepair::getRoomId, roomId);
        }
        if (status != null) {
            wrapper.eq(DormRepair::getStatus, status);
        } else if (statusList != null && !statusList.isEmpty()) {
            String[] parts = statusList.split(",");
            Integer[] statuses = new Integer[parts.length];
            for (int i = 0; i < parts.length; i++) {
                statuses[i] = Integer.valueOf(parts[i].trim());
            }
            wrapper.in(DormRepair::getStatus, (Object[]) statuses);
        }
        if (userId != null) {
            wrapper.eq(DormRepair::getUserId, userId);
        }
        if (repairPerson != null && !repairPerson.isEmpty()) {
            wrapper.eq(DormRepair::getRepairPerson, repairPerson);
        }
        wrapper.orderByDesc(DormRepair::getCreateTime);
        return repairMapper.selectPage(page, wrapper);
    }

    public void addRepair(DormRepair repair) {
        repair.setStatus(0);
        repairMapper.insert(repair);
    }

    public List<DormAllocation> listAllAllocations() {
        LambdaQueryWrapper<DormAllocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DormAllocation::getCreateTime);
        return allocationMapper.selectList(wrapper);
    }

    public List<DormRepair> listAllRepairs() {
        LambdaQueryWrapper<DormRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DormRepair::getCreateTime);
        return repairMapper.selectList(wrapper);
    }

    public void handleRepair(Long id, Integer status, String repairPerson, String repairResult) {
        DormRepair repair = repairMapper.selectById(id);
        if (repair == null) {
            throw new RuntimeException("维修记录不存在");
        }
        repair.setStatus(status);
        repair.setRepairPerson(repairPerson);
        repair.setRepairResult(repairResult);
        if (status == 2) {
            repair.setRepairTime(java.time.LocalDateTime.now());
        }
        repairMapper.updateById(repair);
    }

    public void evaluateRepair(Long id) {
        DormRepair repair = repairMapper.selectById(id);
        if (repair != null) {
            repair.setEvaluated(1);
            repairMapper.updateById(repair);
        }
    }
}
