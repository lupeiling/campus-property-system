package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.dto.PasswordDTO;
import com.campus.property.dto.UserDTO;
import com.campus.property.entity.User;
import com.campus.property.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public IPage<User> listUsers(int current, int size, String keyword, String role) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public void addUser(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userMapper.updateById(user);
    }

    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public void changePassword(PasswordDTO passwordDTO) {
        User user = userMapper.selectById(passwordDTO.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userMapper.updateById(user);
    }

    public void updateProfile(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRealName(userDTO.getRealName());
        user.setGender(userDTO.getGender());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        userMapper.updateById(user);
    }
}
