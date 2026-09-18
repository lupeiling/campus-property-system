package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.entity.Role;
import com.campus.property.entity.SysLog;
import com.campus.property.mapper.RoleMapper;
import com.campus.property.mapper.SysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SysLogMapper logMapper;

    public List<Role> listRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getId);
        return roleMapper.selectList(wrapper);
    }

    public IPage<SysLog> listLogs(int current, int size, String username, String operation) {
        Page<SysLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysLog::getUsername, username);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like(SysLog::getOperation, operation);
        }
        wrapper.orderByDesc(SysLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    public void addLog(SysLog log) {
        logMapper.insert(log);
    }
}
