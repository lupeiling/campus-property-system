package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.entity.*;
import com.campus.property.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenService {

    @Autowired
    private CanteenMapper canteenMapper;

    @Autowired
    private CanteenDishMapper dishMapper;

    @Autowired
    private CanteenConsumeMapper consumeMapper;

    @Autowired
    private CanteenInspectionMapper inspectionMapper;

    public IPage<Canteen> listCanteens(int current, int size, String keyword) {
        Page<Canteen> page = new Page<>(current, size);
        LambdaQueryWrapper<Canteen> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Canteen::getCanteenName, keyword);
        }
        wrapper.orderByDesc(Canteen::getCreateTime);
        return canteenMapper.selectPage(page, wrapper);
    }

    public void addCanteen(Canteen canteen) {
        canteenMapper.insert(canteen);
    }

    public void updateCanteen(Canteen canteen) {
        canteenMapper.updateById(canteen);
    }

    public void deleteCanteen(Long id) {
        canteenMapper.deleteById(id);
    }

    public IPage<CanteenDish> listDishes(int current, int size, Long canteenId, String category) {
        Page<CanteenDish> page = new Page<>(current, size);
        LambdaQueryWrapper<CanteenDish> wrapper = new LambdaQueryWrapper<>();
        if (canteenId != null) {
            wrapper.eq(CanteenDish::getCanteenId, canteenId);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(CanteenDish::getCategory, category);
        }
        wrapper.orderByDesc(CanteenDish::getCreateTime);
        return dishMapper.selectPage(page, wrapper);
    }

    public void addDish(CanteenDish dish) {
        dishMapper.insert(dish);
    }

    public void updateDish(CanteenDish dish) {
        dishMapper.updateById(dish);
    }

    public void deleteDish(Long id) {
        dishMapper.deleteById(id);
    }

    public IPage<CanteenConsume> listConsumes(int current, int size, Long userId, Long canteenId) {
        Page<CanteenConsume> page = new Page<>(current, size);
        LambdaQueryWrapper<CanteenConsume> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(CanteenConsume::getUserId, userId);
        }
        if (canteenId != null) {
            wrapper.eq(CanteenConsume::getCanteenId, canteenId);
        }
        wrapper.orderByDesc(CanteenConsume::getConsumeTime);
        return consumeMapper.selectPage(page, wrapper);
    }

    public List<CanteenConsume> listAllConsumes() {
        LambdaQueryWrapper<CanteenConsume> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CanteenConsume::getConsumeTime);
        return consumeMapper.selectList(wrapper);
    }

    public void addConsume(CanteenConsume consume) {
        consumeMapper.insert(consume);
    }

    public IPage<CanteenInspection> listInspections(int current, int size, Long canteenId) {
        Page<CanteenInspection> page = new Page<>(current, size);
        LambdaQueryWrapper<CanteenInspection> wrapper = new LambdaQueryWrapper<>();
        if (canteenId != null) {
            wrapper.eq(CanteenInspection::getCanteenId, canteenId);
        }
        wrapper.orderByDesc(CanteenInspection::getCreateTime);
        return inspectionMapper.selectPage(page, wrapper);
    }

    public void addInspection(CanteenInspection inspection) {
        inspectionMapper.insert(inspection);
    }
}
