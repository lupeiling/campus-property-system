package com.campus.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.Role;
import com.campus.property.entity.SysLog;
import com.campus.property.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
@PreAuthorize("hasRole('ADMIN')")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/role/list")
    public Result<List<Role>> listRoles() {
        return Result.success(systemService.listRoles());
    }

    @GetMapping("/log/list")
    public Result<PageResult<SysLog>> listLogs(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation) {
        IPage<SysLog> page = systemService.listLogs(current, size, username, operation);
        return Result.success(new PageResult<>(page));
    }
}
