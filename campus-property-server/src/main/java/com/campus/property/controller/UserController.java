package com.campus.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.dto.PasswordDTO;
import com.campus.property.dto.UserDTO;
import com.campus.property.entity.User;
import com.campus.property.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        IPage<User> page = userService.listUsers(current, size, keyword, role);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody PasswordDTO passwordDTO) {
        userService.changePassword(passwordDTO);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserDTO userDTO) {
        userService.updateProfile(userDTO);
        return Result.success();
    }
}
