# 高校后勤物业管理系统 - 部署文档

## 一、项目概述

本系统采用前后端分离架构，后端基于 Spring Boot 2.7.18 + MyBatis-Plus + MySQL 8.0，前端基于 Vue.js 2 + Element UI。

## 二、环境要求

| 环境      | 版本要求    |
| ------- | ------- |
| JDK     | 11.0.6+ |
| Maven   | 3.6+    |
| MySQL   | 8.0+    |
| Node.js | 14.x+   |
| npm     | 6.x+    |

## 三、数据库配置

### 3.1 创建数据库

```bash
mysql -u root -p    //already
```

执行初始化脚本：

```bash
source /path/to/campus-property-server/sql/init.sql       //already
```

### 3.2 修改数据库连接

编辑 `campus-property-server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_property?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 你的数据库密码         //already
```

## 四、后端部署

### 4.1 编译打包

```bash
cd campus-property-server
mvn clean package -DskipTests              
```

### 4.2 运行

```bash
java -jar target/campus-property-server-1.0.0.jar       
```

后端服务默认运行在 `http://localhost:8088`

### 4.3 开发模式运行

```bash
cd campus-property-server
mvn spring-boot:run
```

## 五、前端部署

### 5.1 安装依赖

```bash
cd campus-property-web
npm install
```

### 5.2 开发模式运行

```bash
powershell -ExecutionPolicy Bypass -Command "cd D:\graduation_project\campus-property-web; npm run serve"        
```

前端开发服务器默认运行在 `http://localhost:8080`，已配置代理转发 `/api` 请求到后端。

### 5.3 生产构建

```bash
npm run build
```

构建产物在 `dist` 目录，可部署到 Nginx 等 Web 服务器。

### 5.4 Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/campus-property-web/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 六、测试账号

系统已内置以下测试账号，密码均为 `123456`：

| 用户名       | 密码     | 角色    | 说明           |
| --------- | ------ | ----- | ------------ |
| admin     | 123456 | 系统管理员 | 拥有全部功能权限     |
| staff01   | 123456 | 物业人员  | 拥有后勤管理相关权限   |
| student01 | 123456 | 学生    | 拥有报修、查看等基础权限 |

### 权限说明

- **管理员(ADMIN)**：可访问所有功能模块，包括用户管理、系统管理
- **物业人员(STAFF)**：可访问宿舍管理、餐饮管理、设施管理、物资管理、维修管理等
- **教师(TEACHER)**：可查看信息、提交报修申请
- **学生(STUDENT)**：可查看信息、提交报修申请

## 七、项目结构

### 后端结构

```
campus-property-server/
├── pom.xml
├── sql/
│   └── init.sql                    # 数据库初始化脚本
└── src/main/
    ├── java/com/campus/property/
    │   ├── CampusPropertyApplication.java  # 启动类
    │   ├── common/                  # 通用类（Result, PageResult, Constants）
    │   ├── config/                  # 配置类（Security, MyBatis-Plus, 异常处理）
    │   ├── controller/              # 控制器（7个模块 + 认证）
    │   ├── dto/                     # 数据传输对象
    │   ├── entity/                  # 实体类（对应数据库表）
    │   ├── mapper/                  # MyBatis-Plus Mapper接口
    │   ├── security/                # JWT认证与Spring Security
    │   └── service/                 # 业务逻辑层
    └── resources/
        └── application.yml          # 配置文件
```

### 前端结构

```
campus-property-web/
├── package.json
├── vue.config.js
├── public/
│   └── index.html
└── src/
    ├── main.js                     # 入口文件
    ├── App.vue                     # 根组件
    ├── api/                        # API接口定义
    ├── router/                     # 路由配置
    ├── store/                      # Vuex状态管理
    ├── utils/                      # 工具函数（axios封装）
    └── views/                      # 页面组件
        ├── Login.vue               # 登录页
        ├── Register.vue            # 注册页
        ├── Layout.vue              # 布局框架
        ├── Dashboard.vue           # 首页仪表盘
        ├── Profile.vue             # 个人信息
        ├── user/                   # 用户管理
        ├── dorm/                   # 宿舍管理
        ├── canteen/                # 餐饮管理
        ├── facility/               # 设施管理
        ├── material/               # 物资管理
        ├── repair/                 # 维修管理
        └── system/                 # 系统管理
```

## 八、API接口列表

### 认证模块 `/api/auth`

- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- GET `/api/auth/current` - 获取当前用户信息

### 用户管理 `/api/user`

- GET `/api/user/list` - 用户列表（分页）
- GET `/api/user/{id}` - 用户详情
- POST `/api/user` - 新增用户
- PUT `/api/user` - 修改用户
- DELETE `/api/user/{id}` - 删除用户
- PUT `/api/user/password` - 修改密码
- PUT `/api/user/profile` - 修改个人信息

### 宿舍管理 `/api/dorm`

- GET `/api/dorm/building/list` - 宿舍楼列表
- POST/PUT/DELETE `/api/dorm/building` - 宿舍楼增删改
- GET `/api/dorm/room/list` - 房间列表
- POST/PUT/DELETE `/api/dorm/room` - 房间增删改
- GET `/api/dorm/allocation/list` - 分配列表
- POST `/api/dorm/allocation` - 分配宿舍
- PUT `/api/dorm/allocation/{id}/checkout` - 退宿
- GET `/api/dorm/repair/list` - 维修列表
- POST/PUT `/api/dorm/repair` - 维修申请与处理

### 餐饮管理 `/api/canteen`

- 食堂、菜品、消费记录、卫生检查的CRUD接口

### 设施管理 `/api/facility`

- 设施信息、设施报修的CRUD接口

### 物资管理 `/api/material`

- 物资、分类、采购、领用的CRUD接口

### 维修管理 `/api/repair`

- 工单提交、审核、派工、维修、完成、评价接口

### 系统管理 `/api/system`

- 角色列表、系统日志查询接口

