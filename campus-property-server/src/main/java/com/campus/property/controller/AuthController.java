package com.campus.property.controller;

import com.campus.property.common.Result;
import com.campus.property.dto.LoginDTO;
import com.campus.property.dto.RegisterDTO;
import com.campus.property.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        Map<String, Object> data = authService.login(loginDTO);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success();
    }

    @GetMapping("/current")
    public Result<?> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
}
