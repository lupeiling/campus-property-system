package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.Feedback;
import com.campus.property.entity.User;
import com.campus.property.mapper.FeedbackMapper;
import com.campus.property.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, auth.getName());
        User user = userMapper.selectOne(wrapper);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Feedback> page = feedbackMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<Feedback>().orderByDesc(Feedback::getCreateTime));

        java.util.List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Feedback f : page.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", f.getId());
            item.put("userId", f.getUserId());
            item.put("type", f.getType());
            item.put("content", f.getContent());
            item.put("contact", f.getContact());
            item.put("reply", f.getReply());
            item.put("replyBy", f.getReplyBy());
            item.put("createTime", f.getCreateTime());

            User user = userMapper.selectById(f.getUserId());
            if (user != null) {
                item.put("userName", user.getRealName() != null ? user.getRealName() : user.getUsername());
                item.put("userUsername", user.getUsername());
                item.put("userRole", user.getRole());
            }
            records.add(item);
        }

        PageResult<Map<String, Object>> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Feedback feedback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getCurrentUserId(auth);
        if (userId != null) {
            feedback.setUserId(userId);
        }
        feedbackMapper.insert(feedback);
        return Result.success();
    }

    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback != null) {
            feedback.setReply(body.get("reply"));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            feedback.setReplyBy(auth != null ? auth.getName() : null);
            feedbackMapper.updateById(feedback);
        }
        return Result.success();
    }
}
