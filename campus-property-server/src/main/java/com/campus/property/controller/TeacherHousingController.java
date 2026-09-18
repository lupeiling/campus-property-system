package com.campus.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.common.PageResult;
import com.campus.property.common.Result;
import com.campus.property.entity.TeacherHousing;
import com.campus.property.entity.User;
import com.campus.property.mapper.TeacherHousingMapper;
import com.campus.property.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/teacher/housing")
public class TeacherHousingController {

    @Autowired
    private TeacherHousingMapper teacherHousingMapper;

    @Autowired
    private UserMapper userMapper;

    private static final List<Map<String, Object>> HOUSING_BENEFIT_LIST = new ArrayList<>();
    static {
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "院士"); put("housingType", "专家独栋"); put("apartmentName", "院士楼");
            put("location", "东区1-10栋"); put("area", 200); put("rentPerSqm", 5); put("propertyFeePerSqm", 3);
            put("schoolSubsidy", 1400); put("selfPay", 200);
            put("formula", "200×5+200×3-1400=200");
        }});
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "杰青"); put("housingType", "高级复式公寓"); put("apartmentName", "人才公寓");
            put("location", "南区1-5栋"); put("area", 140); put("rentPerSqm", 8); put("propertyFeePerSqm", 3);
            put("schoolSubsidy", 1000); put("selfPay", 540);
            put("formula", "140×8+140×3-1000=540");
        }});
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "教授"); put("housingType", "三室两厅套房"); put("apartmentName", "教授公寓");
            put("location", "南区6-10栋"); put("area", 120); put("rentPerSqm", 10); put("propertyFeePerSqm", 2.5);
            put("schoolSubsidy", 800); put("selfPay", 700);
            put("formula", "120×10+120×2.5-800=700");
        }});
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "副教授"); put("housingType", "两室两厅"); put("apartmentName", "青年公寓");
            put("location", "西区1-5栋"); put("area", 90); put("rentPerSqm", 12); put("propertyFeePerSqm", 2.5);
            put("schoolSubsidy", 500); put("selfPay", 805);
            put("formula", "90×12+90×2.5-500=805");
        }});
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "讲师/博士"); put("housingType", "一室一厅"); put("apartmentName", "青年公寓");
            put("location", "西区6-10座"); put("area", 50); put("rentPerSqm", 15); put("propertyFeePerSqm", 2);
            put("schoolSubsidy", 200); put("selfPay", 650);
            put("formula", "50×15+50×2-200=650");
        }});
        HOUSING_BENEFIT_LIST.add(new LinkedHashMap<String, Object>() {{
            put("titleLevel", "其他教职工"); put("housingType", "两室一厅"); put("apartmentName", "周转房");
            put("location", "北区1-10栋"); put("area", 70); put("rentPerSqm", 18); put("propertyFeePerSqm", 2);
            put("schoolSubsidy", 100); put("selfPay", 1300);
            put("formula", "70×18+70×2-100=1300");
        }});
    }

    @GetMapping("/benefit")
    public Result<Map<String, Object>> getHousingBenefit() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, auth.getName());
        User user = userMapper.selectOne(wrapper);

        String title = user != null && user.getTitle() != null ? user.getTitle() : "其他教职工";
        Map<String, Object> matched = null;
        for (Map<String, Object> item : HOUSING_BENEFIT_LIST) {
            if (title.equals(item.get("titleLevel"))) {
                matched = item;
                break;
            }
        }
        if (matched == null) {
            matched = HOUSING_BENEFIT_LIST.get(HOUSING_BENEFIT_LIST.size() - 1);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("benefit", matched);
        return Result.success(data);
    }

    @GetMapping("/benefit/list")
    public Result<List<Map<String, Object>>> getAllBenefits() {
        return Result.success(HOUSING_BENEFIT_LIST);
    }

    @GetMapping("/list")
    public Result<PageResult<TeacherHousing>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

        LambdaQueryWrapper<TeacherHousing> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && !isStaff) {
            if (userId != null) {
                wrapper.eq(TeacherHousing::getUserId, userId);
            } else {
                LambdaQueryWrapper<User> uwrap = new LambdaQueryWrapper<>();
                uwrap.eq(User::getUsername, auth.getName());
                User user = userMapper.selectOne(uwrap);
                if (user != null) wrapper.eq(TeacherHousing::getUserId, user.getId());
            }
        } else if (userId != null) {
            wrapper.eq(TeacherHousing::getUserId, userId);
        }
        wrapper.orderByDesc(TeacherHousing::getCreateTime);

        IPage<TeacherHousing> page = teacherHousingMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> add(@RequestBody TeacherHousing housing) {
        teacherHousingMapper.insert(housing);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<Void> update(@RequestBody TeacherHousing housing) {
        teacherHousingMapper.updateById(housing);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        teacherHousingMapper.deleteById(id);
        return Result.success();
    }
}
