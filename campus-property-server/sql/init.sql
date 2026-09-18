CREATE DATABASE IF NOT EXISTS campus_property DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_property;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(200) NOT NULL COMMENT '密码',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `role` varchar(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN/STAFF/TEACHER/STUDENT',
    `department` varchar(100) DEFAULT NULL COMMENT '院系/部门',
    `title` varchar(50) DEFAULT NULL COMMENT '职称',
    `student_no` varchar(50) DEFAULT NULL COMMENT '学号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 宿舍楼表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_building`;
CREATE TABLE `dorm_building` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `building_name` varchar(50) NOT NULL COMMENT '楼栋名称',
    `building_no` varchar(20) NOT NULL COMMENT '楼栋编号',
    `floors` int NOT NULL DEFAULT 1 COMMENT '楼层数',
    `rooms_per_floor` int NOT NULL DEFAULT 10 COMMENT '每层房间数',
    `capacity` int NOT NULL DEFAULT 4 COMMENT '每间容量',
    `gender_type` tinyint NOT NULL DEFAULT 0 COMMENT '0-混合 1-男生 2-女生',
    `manager_id` bigint DEFAULT NULL COMMENT '管理员ID',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_no` (`building_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- ----------------------------
-- 宿舍房间表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_room`;
CREATE TABLE `dorm_room` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `building_id` bigint NOT NULL COMMENT '楼栋ID',
    `room_no` varchar(20) NOT NULL COMMENT '房间号',
    `floor` int NOT NULL COMMENT '楼层',
    `capacity` int NOT NULL DEFAULT 4 COMMENT '容量',
    `room_type` varchar(20) DEFAULT '四人寝' COMMENT '房间类型:二人寝/四人寝/六人寝',
    `current_count` int NOT NULL DEFAULT 0 COMMENT '当前入住人数',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-不可用 1-可用',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍房间表';

-- ----------------------------
-- 宿舍分配表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_allocation`;
CREATE TABLE `dorm_allocation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `room_id` bigint NOT NULL COMMENT '房间ID',
    `bed_no` int NOT NULL COMMENT '床位号',
    `check_in_date` date DEFAULT NULL COMMENT '入住日期',
    `check_out_date` date DEFAULT NULL COMMENT '退宿日期',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-已退宿 1-在住',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍分配表';

-- ----------------------------
-- 宿舍维修申请表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_repair`;
CREATE TABLE `dorm_repair` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `room_id` bigint DEFAULT NULL COMMENT '房间ID',
    `user_id` bigint NOT NULL COMMENT '申请人ID',
    `title` varchar(100) NOT NULL COMMENT '维修标题',
    `description` text COMMENT '维修描述',
    `images` varchar(1000) DEFAULT NULL COMMENT '图片(逗号分隔)',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待处理 1-处理中 2-已完成 3-已拒绝',
    `repair_person` varchar(50) DEFAULT NULL COMMENT '维修人员',
    `repair_result` varchar(500) DEFAULT NULL COMMENT '维修结果',
    `repair_time` datetime DEFAULT NULL COMMENT '维修完成时间',
    `evaluated` tinyint NOT NULL DEFAULT 0 COMMENT '是否已评价 0-未评价 1-已评价',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍维修申请表';

-- ----------------------------
-- 食堂表
-- ----------------------------
DROP TABLE IF EXISTS `canteen`;
CREATE TABLE `canteen` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `canteen_name` varchar(50) NOT NULL COMMENT '食堂名称',
    `location` varchar(200) DEFAULT NULL COMMENT '位置',
    `floors` int NOT NULL DEFAULT 1 COMMENT '楼层数',
    `manager_id` bigint DEFAULT NULL COMMENT '管理员ID',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂表';

-- ----------------------------
-- 菜品表
-- ----------------------------
DROP TABLE IF EXISTS `canteen_dish`;
CREATE TABLE `canteen_dish` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `canteen_id` bigint NOT NULL COMMENT '食堂ID',
    `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
    `category` varchar(50) DEFAULT NULL COMMENT '分类',
    `price` decimal(10,2) NOT NULL COMMENT '价格',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `image` varchar(255) DEFAULT NULL COMMENT '图片',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-下架 1-上架',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_canteen_id` (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ----------------------------
-- 餐饮消费记录表
-- ----------------------------
DROP TABLE IF EXISTS `canteen_consume`;
CREATE TABLE `canteen_consume` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `canteen_id` bigint NOT NULL COMMENT '食堂ID',
    `dish_id` bigint DEFAULT NULL COMMENT '菜品ID',
    `amount` decimal(10,2) NOT NULL COMMENT '消费金额',
    `consume_time` datetime NOT NULL COMMENT '消费时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_canteen_id` (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐饮消费记录表';

-- ----------------------------
-- 食堂卫生检查记录表
-- ----------------------------
DROP TABLE IF EXISTS `canteen_inspection`;
CREATE TABLE `canteen_inspection` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `canteen_id` bigint NOT NULL COMMENT '食堂ID',
    `inspector_id` bigint NOT NULL COMMENT '检查人ID',
    `inspect_date` date NOT NULL COMMENT '检查日期',
    `score` int DEFAULT NULL COMMENT '评分(0-100)',
    `hygiene_status` tinyint NOT NULL DEFAULT 1 COMMENT '0-不合格 1-合格 2-优秀',
    `issues` text COMMENT '发现问题',
    `rectification` text COMMENT '整改措施',
    `images` varchar(1000) DEFAULT NULL COMMENT '图片(逗号分隔)',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_canteen_id` (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂卫生检查记录表';

-- ----------------------------
-- 校园设施表
-- ----------------------------
DROP TABLE IF EXISTS `facility`;
CREATE TABLE `facility` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `facility_name` varchar(100) NOT NULL COMMENT '设施名称',
    `facility_no` varchar(20) NOT NULL COMMENT '设施编号',
    `category` varchar(50) DEFAULT NULL COMMENT '设施分类',
    `location` varchar(200) DEFAULT NULL COMMENT '位置',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-不可用 1-正常 2-维修中',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_facility_no` (`facility_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园设施表';

-- ----------------------------
-- 设施报修表
-- ----------------------------
DROP TABLE IF EXISTS `facility_repair`;
CREATE TABLE `facility_repair` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `facility_id` bigint NOT NULL COMMENT '设施ID',
    `user_id` bigint NOT NULL COMMENT '报修人ID',
    `title` varchar(100) NOT NULL COMMENT '报修标题',
    `description` text COMMENT '问题描述',
    `images` varchar(1000) DEFAULT NULL COMMENT '图片',
    `urgency` tinyint NOT NULL DEFAULT 1 COMMENT '紧急程度 1-普通 2-紧急 3-非常紧急',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待处理 1-处理中 2-已完成 3-已拒绝',
    `repair_person` varchar(50) DEFAULT NULL COMMENT '维修人员',
    `repair_result` varchar(500) DEFAULT NULL COMMENT '维修结果',
    `repair_time` datetime DEFAULT NULL COMMENT '完成时间',
    `evaluated` tinyint NOT NULL DEFAULT 0 COMMENT '是否已评价 0-未评价 1-已评价',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_facility_id` (`facility_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施报修表';

-- ----------------------------
-- 物资分类表
-- ----------------------------
DROP TABLE IF EXISTS `material_category`;
CREATE TABLE `material_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_name` varchar(50) NOT NULL COMMENT '分类名称',
    `description` varchar(200) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- ----------------------------
-- 物资表
-- ----------------------------
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_name` varchar(100) NOT NULL COMMENT '物资名称',
    `material_no` varchar(20) NOT NULL COMMENT '物资编号',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID',
    `specification` varchar(200) DEFAULT NULL COMMENT '规格',
    `unit` varchar(20) DEFAULT NULL COMMENT '单位',
    `stock_quantity` int NOT NULL DEFAULT 0 COMMENT '库存数量',
    `min_quantity` int NOT NULL DEFAULT 0 COMMENT '最低库存',
    `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material_no` (`material_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资表';

-- ----------------------------
-- 物资采购申请表
-- ----------------------------
DROP TABLE IF EXISTS `material_purchase`;
CREATE TABLE `material_purchase` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `purchase_no` varchar(30) NOT NULL COMMENT '采购单号',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `title` varchar(100) NOT NULL COMMENT '采购标题',
    `total_amount` decimal(12,2) DEFAULT NULL COMMENT '总金额',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待审批 1-已审批 2-已拒绝 3-采购中 4-已完成',
    `approve_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
    `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_purchase_no` (`purchase_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资采购申请表';

-- ----------------------------
-- 采购明细表
-- ----------------------------
DROP TABLE IF EXISTS `material_purchase_item`;
CREATE TABLE `material_purchase_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `purchase_id` bigint NOT NULL COMMENT '采购单ID',
    `material_id` bigint NOT NULL COMMENT '物资ID',
    `quantity` int NOT NULL COMMENT '数量',
    `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
    `subtotal` decimal(12,2) DEFAULT NULL COMMENT '小计',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_purchase_id` (`purchase_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购明细表';

-- ----------------------------
-- 物资领用记录表
-- ----------------------------
DROP TABLE IF EXISTS `material_usage`;
CREATE TABLE `material_usage` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_id` bigint NOT NULL COMMENT '物资ID',
    `user_id` bigint NOT NULL COMMENT '领用人ID',
    `quantity` int NOT NULL COMMENT '领用数量',
    `purpose` varchar(200) DEFAULT NULL COMMENT '用途',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-已归还 1-使用中',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material_id` (`material_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资领用记录表';

-- ----------------------------
-- 维修工单表
-- ----------------------------
DROP TABLE IF EXISTS `repair_order`;
CREATE TABLE `repair_order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no` varchar(30) NOT NULL COMMENT '工单编号',
    `title` varchar(100) NOT NULL COMMENT '维修标题',
    `description` text COMMENT '维修描述',
    `images` varchar(1000) DEFAULT NULL COMMENT '图片',
    `source_type` tinyint NOT NULL COMMENT '来源类型 1-宿舍 2-设施',
    `source_id` bigint DEFAULT NULL COMMENT '来源ID',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `category` varchar(50) DEFAULT NULL COMMENT '维修分类',
    `urgency` tinyint NOT NULL DEFAULT 1 COMMENT '紧急程度 1-普通 2-紧急 3-非常紧急',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待审核 1-已审核 2-已派工 3-维修中 4-已完成 5-已拒绝',
    `repair_person_id` bigint DEFAULT NULL COMMENT '维修人员ID',
    `assign_time` datetime DEFAULT NULL COMMENT '派工时间',
    `start_time` datetime DEFAULT NULL COMMENT '开始维修时间',
    `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
    `repair_result` text COMMENT '维修结果',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修工单表';

-- ----------------------------
-- 维修评价表
-- ----------------------------
DROP TABLE IF EXISTS `repair_evaluation`;
CREATE TABLE `repair_evaluation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id` bigint NOT NULL COMMENT '工单ID',
    `user_id` bigint NOT NULL COMMENT '评价人ID',
    `score` int NOT NULL COMMENT '评分(1-5)',
    `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修评价表';

-- ----------------------------
-- 系统日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
    `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` varchar(200) NOT NULL COMMENT '操作内容',
    `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
    `params` text COMMENT '请求参数',
    `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(20) NOT NULL COMMENT '角色编码',
    `description` varchar(200) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 意见反馈表
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `type` varchar(50) DEFAULT NULL COMMENT '反馈类型',
    `content` text NOT NULL COMMENT '反馈内容',
    `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
    `reply` text COMMENT '回复内容',
    `reply_by` varchar(50) DEFAULT NULL COMMENT '回复人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈表';

-- ----------------------------
-- 学生费用表
-- ----------------------------
DROP TABLE IF EXISTS `student_fee`;
CREATE TABLE `student_fee` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '学生ID',
    `fee_type` varchar(50) NOT NULL COMMENT '费用类型:住宿费/水费/电费',
    `amount` decimal(10,2) NOT NULL COMMENT '金额',
    `semester` varchar(20) DEFAULT NULL COMMENT '学期',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待缴 1-已缴',
    `due_date` date DEFAULT NULL COMMENT '截止日期',
    `pay_time` datetime DEFAULT NULL COMMENT '缴费时间',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生费用表';

-- ----------------------------
-- 人员聘用表
-- ----------------------------
DROP TABLE IF EXISTS `staff_employment`;
CREATE TABLE `staff_employment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NOT NULL COMMENT '姓名',
    `position` varchar(50) NOT NULL COMMENT '岗位',
    `phone` varchar(20) DEFAULT NULL COMMENT '电话',
    `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资标准',
    `hire_date` date DEFAULT NULL COMMENT '入职时间',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-离职 1-在职',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员聘用表';

-- ----------------------------
-- 教师住房表
-- ----------------------------
DROP TABLE IF EXISTS `teacher_housing`;
CREATE TABLE `teacher_housing` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '教师ID',
    `apartment_name` varchar(100) NOT NULL COMMENT '公寓名称',
    `location` varchar(200) DEFAULT NULL COMMENT '位置',
    `title_level` varchar(50) DEFAULT NULL COMMENT '职称等级',
    `rent` decimal(10,2) DEFAULT NULL COMMENT '月租金',
    `property_fee` decimal(10,2) DEFAULT NULL COMMENT '物业费/月',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-退房 1-在住',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师住房表';

-- ----------------------------
-- 宿舍审核表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_audit`;
CREATE TABLE `dorm_audit` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '申请人ID',
    `audit_type` varchar(20) NOT NULL COMMENT '审核类型:入住/退宿/调宿',
    `room_id` bigint DEFAULT NULL COMMENT '房间ID',
    `bed_no` int DEFAULT NULL COMMENT '床位号',
    `new_room_id` bigint DEFAULT NULL COMMENT '新房间ID',
    `new_bed_no` int DEFAULT NULL COMMENT '新床位号',
    `reason` varchar(500) DEFAULT NULL COMMENT '申请原因',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '0-待审核 1-已通过 2-已拒绝',
    `audit_by` varchar(50) DEFAULT NULL COMMENT '审核人',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `audit_remark` varchar(200) DEFAULT NULL COMMENT '审核备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍审核表';

-- ----------------------------
-- 宿管排班表
-- ----------------------------
DROP TABLE IF EXISTS `dorm_schedule`;
CREATE TABLE `dorm_schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `manager_name` varchar(50) NOT NULL COMMENT '宿管姓名',
    `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
    `schedule_date` date NOT NULL COMMENT '值班日期',
    `shift` varchar(20) DEFAULT '白班' COMMENT '班次',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`),
    KEY `idx_schedule_date` (`schedule_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿管排班表';

-- ----------------------------
-- 清洁区域分配表
-- ----------------------------
DROP TABLE IF EXISTS `cleaning_assignment`;
CREATE TABLE `cleaning_assignment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cleaner_name` varchar(50) NOT NULL COMMENT '清洁人员姓名',
    `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
    `week_start` date NOT NULL COMMENT '周开始日期',
    `week_end` date NOT NULL COMMENT '周结束日期',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-已完成 1-进行中',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清洁区域分配表';

-- ----------------------------
-- 打扫记录表
-- ----------------------------
DROP TABLE IF EXISTS `cleaning_record`;
CREATE TABLE `cleaning_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `assignment_id` bigint NOT NULL COMMENT '分配ID',
    `cleaner_name` varchar(50) NOT NULL COMMENT '清洁人员',
    `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
    `clean_date` date NOT NULL COMMENT '打扫日期',
    `score` int DEFAULT NULL COMMENT '评分0-100',
    `issues` varchar(500) DEFAULT NULL COMMENT '问题',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_assignment_id` (`assignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打扫记录表';

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES
('系统管理员', 'ADMIN', '系统最高权限管理员'),
('物业人员', 'STAFF', '后勤物业工作人员'),
('教师', 'TEACHER', '教职工用户'),
('学生', 'STUDENT', '学生用户');

-- ----------------------------
-- 初始化用户数据 (密码均为 123456 的BCrypt加密)
-- ----------------------------
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `gender`, `phone`, `email`, `role`, `department`, `student_no`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, '13800000001', 'admin@campus.edu.cn', 'ADMIN', '信息中心', NULL, 1),
('staff01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张物业', 1, '13800000002', 'staff01@campus.edu.cn', 'STAFF', '后勤管理处', NULL, 1),
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', 2, '13800000003', 'teacher01@campus.edu.cn', 'TEACHER', '计算机学院', NULL, 1),
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王同学', 1, '13800000004', 'student01@campus.edu.cn', 'STUDENT', '计算机学院', '2021001001', 1);

-- ----------------------------
-- 初始化宿舍楼数据
-- ----------------------------
INSERT INTO `dorm_building` (`building_name`, `building_no`, `floors`, `rooms_per_floor`, `capacity`, `gender_type`) VALUES
('1号宿舍楼', 'D001', 6, 20, 4, 1),
('2号宿舍楼', 'D002', 6, 20, 4, 2),
('3号宿舍楼', 'D003', 6, 20, 4, 1);

-- ----------------------------
-- 初始化食堂数据
-- ----------------------------
INSERT INTO `canteen` (`canteen_name`, `location`, `floors`) VALUES
('第一食堂', '校园东区', 2),
('第二食堂', '校园西区', 3),
('教工食堂', '校园中心', 2);

-- ----------------------------
-- 初始化物资分类
-- ----------------------------
INSERT INTO `material_category` (`category_name`, `description`) VALUES
('办公用品', '日常办公消耗品'),
('清洁用品', '清洁卫生用品'),
('维修工具', '维修所需工具及配件'),
('电器设备', '电器类设备');

-- ----------------------------
-- 初始化物资数据
-- ----------------------------
INSERT INTO `material` (`material_name`, `material_no`, `category_id`, `specification`, `unit`, `stock_quantity`, `min_quantity`, `unit_price`) VALUES
('A4打印纸', 'M001', 1, '70g 500张/包', '包', 100, 20, 25.00),
('中性笔', 'M002', 1, '0.5mm黑色', '支', 200, 50, 2.00),
('拖把', 'M003', 2, '不锈钢杆', '把', 30, 10, 15.00),
('消毒液', 'M004', 2, '500ml', '瓶', 50, 15, 8.00),
('电钻', 'M005', 3, '手电钻', '台', 5, 2, 280.00),
('灯泡', 'M006', 3, 'LED 18W', '个', 100, 30, 12.00),
('电风扇', 'M007', 4, '落地扇', '台', 10, 3, 150.00);

-- ----------------------------
-- 初始化校园设施数据
-- ----------------------------
INSERT INTO `facility` (`facility_name`, `facility_no`, `category`, `location`, `status`) VALUES
('图书馆电梯', 'F001', '电梯', '图书馆', 1),
('教学楼A空调', 'F002', '空调', '教学楼A栋', 1),
('操场照明', 'F003', '照明', '操场', 1),
('食堂消防栓', 'F004', '消防', '第一食堂', 1),
('校园监控', 'F005', '监控', '校门', 1);
