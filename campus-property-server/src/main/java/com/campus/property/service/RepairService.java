package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.entity.RepairEvaluation;
import com.campus.property.entity.RepairOrder;
import com.campus.property.mapper.RepairEvaluationMapper;
import com.campus.property.mapper.RepairOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RepairService {

    @Autowired
    private RepairOrderMapper orderMapper;

    @Autowired
    private RepairEvaluationMapper evaluationMapper;

    public IPage<RepairOrder> listOrders(int current, int size, Integer status, Integer sourceType, Long applicantId) {
        Page<RepairOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        if (sourceType != null) {
            wrapper.eq(RepairOrder::getSourceType, sourceType);
        }
        if (applicantId != null) {
            wrapper.eq(RepairOrder::getApplicantId, applicantId);
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return orderMapper.selectPage(page, wrapper);
    }

    public List<RepairOrder> listAllOrders() {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    public RepairOrder getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    public void submitOrder(RepairOrder order) {
        order.setOrderNo("RO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        order.setStatus(0);
        orderMapper.insert(order);
    }

    public void approveOrder(Long id, Integer status) {
        RepairOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    public void assignOrder(Long id, Long repairPersonId) {
        RepairOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(2);
        order.setRepairPersonId(repairPersonId);
        order.setAssignTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    public void startRepair(Long id) {
        RepairOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(3);
        order.setStartTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    public void completeRepair(Long id, String repairResult) {
        RepairOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(4);
        order.setRepairResult(repairResult);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    public void addEvaluation(RepairEvaluation evaluation) {
        evaluationMapper.insert(evaluation);
    }

    public RepairEvaluation getEvaluationByOrderId(Long orderId) {
        LambdaQueryWrapper<RepairEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairEvaluation::getOrderId, orderId);
        return evaluationMapper.selectOne(wrapper);
    }
}
