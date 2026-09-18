---
name: database-connection-implementation
description: 高校后勤物业管理系统5.2数据库连接实现章节内容
metadata:
type: reference
---
5.2 数据库连接实现
5.2.1 application.yml配置
在IntelliJ IDEA 2020.3.2开发工具下，系统采用application.yml配置文件连接数据库。在src/main/resources文件夹下创建application.yml文件，编写数据库连接配置。

数据库连接配置如下：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/campus_property?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    hikari:
      minimum-idle: 5
      maximum-pool-size: 15
      auto-commit: true
      idle-timeout: 30000
      pool-name: CampusPropertyCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1
```

5.2.2 MyBatis-Plus配置
系统采用MyBatis-Plus作为ORM框架，配置如下：

```java
@Configuration
@MapperScan("com.campus.property.mapper")
public class MyBatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 添加性能分析插件
        interceptor.addInnerInterceptor(new PerformanceInnerInterceptor());
        return interceptor;
    }
    
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }
}
```

5.3 系统功能实现
系统实现了四种用户角色：系统管理员、物业人员、教师、学生。不同角色具有不同的操作权限，实现各自的业务流程。

5.3.1 用户登录功能实现
高校后勤物业管理系统采用JWT + Spring Security实现用户认证。登录模块通过用户名和密码验证用户身份，成功后生成JWT token返回给前端，前端将token存储在localStorage中，后续请求在Authorization头中携带token进行身份验证。

（1）用户登录界面实现基于Vue.js + Element UI构建

（2）功能流程图：
- 用户输入用户名和密码
- 前端发送登录请求到后端
- 后端验证用户信息
- 验证成功生成JWT token
- 返回token给前端
- 前端存储token并跳转到主页面

（3）核心代码如下：
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        // 通过用户名查询用户信息
        User user = userService.findByUsername(loginRequest.getUsername());
        
        // 验证用户是否存在
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        // 验证用户状态
        if (!user.getStatus().equals("ACTIVE")) {
            return Result.fail(403, "用户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Result.fail(403, "密码错误");
        }
        
        // 生成JWT token
        String token = JwtTokenUtil.generateToken(user);
        
        // 返回用户信息和token
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        
        return Result.succeed(response);
    }
}
```

代码解析：代码5查询用户信息，代码8验证用户状态，代码11使用BCrypt加密验证密码，代码15生成JWT token，代码21返回登录结果。

5.3.2 用户管理功能实现
（1）用户管理界面基于Element UI的表格组件实现

（2）功能流程：
- 管理员进入用户管理页面
- 系统加载用户列表数据
- 支持按用户名、角色等条件搜索
- 支持新增、编辑、删除用户
- 支持修改用户状态和权限

（3）核心代码如下：
```java
@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/list")
    public Result list(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search) {
        
        // 创建分页对象
        Page<User> pageRequest = Page.of(page, size);
        
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            queryWrapper.like("username", search)
                       .or()
                       .like("real_name", search);
        }
        
        // 执行分页查询
        Page<User> userPage = userService.page(pageRequest, queryWrapper);
        
        return Result.succeed(userPage);
    }
    
    @PostMapping
    public Result create(@RequestBody User user) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.succeed("用户创建成功");
    }
    
    @PutMapping
    public Result update(@RequestBody User user) {
        userService.updateById(user);
        return Result.succeed("用户更新成功");
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.succeed("用户删除成功");
    }
}
```

代码解析：代码11实现分页查询，代码14-18构建搜索条件，代码21执行数据库查询，代码27使用BCrypt加密密码，代码31、35、39分别实现用户的增删改操作。

5.3.3 宿舍管理功能实现
（1）宿舍管理界面包含宿舍楼管理、房间管理、住宿分配等功能模块

（2）功能流程：
- 管理员管理宿舍楼信息
- 管理房间信息和状态
- 分配学生住宿
- 处理入住和退宿申请
- 统计宿舍使用情况

（3）核心代码如下：
```java
@RestController
@RequestMapping("/api/dorm")
public class DormController {
    
    @Autowired
    private DormService dormService;
    
    @GetMapping("/building/list")
    public Result buildingList() {
        List<DormBuilding> buildings = dormService.getAllBuildings();
        return Result.succeed(buildings);
    }
    
    @GetMapping("/room/list")
    public Result roomList(
        @RequestParam Long buildingId,
        @RequestParam(required = false) String roomNumber) {
        
        QueryWrapper<DormRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_id", buildingId);
        
        if (StringUtils.isNotBlank(roomNumber)) {
            queryWrapper.like("room_number", roomNumber);
        }
        
        List<DormRoom> rooms = dormService.list(queryWrapper);
        return Result.succeed(rooms);
    }
    
    @PostMapping("/allocation")
    public Result allocateRoom(@RequestBody RoomAllocationRequest request) {
        dormService.allocateRoom(request.getStudentId(), request.getRoomId());
        return Result.succeed("宿舍分配成功");
    }
    
    @PutMapping("/allocation/{id}/checkout")
    public Result checkout(@PathVariable Long id) {
        dormService.checkout(id);
        return Result.succeed("退宿成功");
    }
}
```

代码解析：代码11获取所有宿舍楼信息，代码14-22根据条件查询房间信息，代码25实现宿舍分配功能，代码29处理退宿申请。

5.3.4 维修管理功能实现
（1）维修管理界面包含工单提交、处理、跟踪、评价等完整流程

（2）功能流程：
- 学生提交维修申请
- 物业人员接收并分配工单
- 维修人员处理工单
- 用户确认维修完成
- 用户评价服务质量

（3）核心代码如下：
```java
@RestController
@RequestMapping("/api/repair")
public class RepairController {
    
    @Autowired
    private RepairService repairService;
    
    @PostMapping
    public Result submitRepair(@RequestBody RepairRequest request, 
                              @RequestHeader("Authorization") String token) {
        // 从token中获取用户信息
        User user = JwtTokenUtil.getUserFromToken(token);
        
        RepairOrder order = new RepairOrder();
        order.setUserId(user.getId());
        order.setDescription(request.getDescription());
        order.setLocation(request.getLocation());
        order.setCategory(request.getCategory());
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        
        repairService.save(order);
        return Result.succeed("维修申请提交成功");
    }
    
    @GetMapping("/list")
    public Result listRepairs(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status) {
        
        Page<RepairOrder> pageRequest = Page.of(page, size);
        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        
        Page<RepairOrder> repairPage = repairService.page(pageRequest, queryWrapper);
        return Result.succeed(repairPage);
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('STAFF')")
    public Result assignRepair(@PathVariable Long id, 
                              @RequestBody AssignRequest request) {
        repairService.assignRepair(id, request.getTechnicianId());
        return Result.succeed("工单分配成功");
    }
    
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('STAFF')")
    public Result completeRepair(@PathVariable Long id) {
        repairService.completeRepair(id);
        return Result.succeed("维修完成");
    }
}
```

代码解析：代码11从JWT token中获取用户信息，代码14-22创建维修工单，代码25提交工单到数据库，代码29-36分页查询工单列表，代码40分配工单给维修人员，代码45标记工单为完成状态。

5.3.5 数据统计功能实现
（1）数据统计界面使用ECharts图表库展示各类统计数据

（2）功能流程：
- 获取各类业务数据
- 数据处理和统计计算
- 生成图表数据
- 前端渲染图表展示

（3）核心代码如下：
```java
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("/dorm-occupancy")
    public Result getDormOccupancyStats() {
        DormOccupancyStats stats = statisticsService.getDormOccupancyStats();
        return Result.succeed(stats);
    }
    
    @GetMapping("/repair-status")
    public Result getRepairStatusStats() {
        RepairStatusStats stats = statisticsService.getRepairStatusStats();
        return Result.succeed(stats);
    }
    
    @GetMapping("/material-consumption")
    public Result getMaterialConsumptionStats(
        @RequestParam String startDate,
        @RequestParam String endDate) {
        MaterialConsumptionStats stats = statisticsService
            .getMaterialConsumptionStats(startDate, endDate);
        return Result.succeed(stats);
    }
}

@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private DormMapper dormMapper;
    
    @Autowired
    private RepairMapper repairMapper;
    
    @Autowired
    private MaterialMapper materialMapper;
    
    public DormOccupancyStats getDormOccupancyStats() {
        // 获取宿舍入住统计数据
        List<DormOccupancyData> data = dormMapper.getOccupancyStats();
        
        DormOccupancyStats stats = new DormOccupancyStats();
        stats.setData(data);
        stats.setTotalRooms(data.stream().mapToInt(DormOccupancyData::getTotalRooms).sum());
        stats.setOccupiedRooms(data.stream().mapToInt(DormOccupancyData::getOccupiedRooms).sum());
        
        return stats;
    }
    
    public RepairStatusStats getRepairStatusStats() {
        // 获取维修工单状态统计
        List<RepairStatusData> data = repairMapper.getStatusStats();
        
        RepairStatusStats stats = new RepairStatusStats();
        stats.setData(data);
        stats.setTotalOrders(data.stream().mapToInt(RepairStatusData::getCount).sum());
        
        return stats;
    }
}
```

代码解析：代码11-13提供宿舍入住率统计接口，代码15-17提供维修状态统计接口，代码19-23提供物资消耗统计接口，代码31-42实现宿舍入住率的具体统计逻辑，代码44-55实现维修工单状态统计。