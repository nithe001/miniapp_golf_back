/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : miniapp_golf

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 24/01/2019 23:01:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `au_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '后台用户表主键',
  `au_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户名',
  `au_password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `au_show_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `au_sex` int(11) DEFAULT NULL COMMENT '性别',
  `au_age` int(11) DEFAULT NULL COMMENT '年龄',
  `au_tel` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `au_email` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  `au_role` bigint(20) DEFAULT NULL COMMENT '角色',
  `au_is_valid` int(11) DEFAULT NULL COMMENT '是否有效',
  `au_create_date` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `au_create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `au_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人姓名',
  `au_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `au_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '更新人名称',
  `au_update_date` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`au_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '后台用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'peihong', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '张三', 1, 20, NULL, '', 1, 1, 1, 1, '1', NULL, NULL, NULL);
INSERT INTO `admin_user` VALUES (2, 'admin', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '123', NULL, NULL, NULL, '', NULL, 1, 1494236192458, 1, 'peihong', NULL, NULL, NULL);
INSERT INTO `admin_user` VALUES (3, 'nmy', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'nmy', NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_group
-- ----------------------------
DROP TABLE IF EXISTS `match_group`;
CREATE TABLE `match_group`  (
  `mg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '比赛分组表主键',
  `mg_match_id` bigint(20) DEFAULT NULL COMMENT '赛事id',
  `mg_group_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组名称',
  `mg_create_user_id` bigint(20) DEFAULT NULL COMMENT '分组创建人id',
  `mg_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组创建人姓名',
  `mg_create_time` bigint(20) DEFAULT NULL COMMENT '分组创建时间',
  `mg_update_user_id` bigint(11) DEFAULT NULL COMMENT '分组更新人id',
  `mg_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组更新人姓名',
  `mg_update_time` bigint(20) DEFAULT NULL COMMENT '分组更新时间',
  PRIMARY KEY (`mg_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '比赛分组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_group
-- ----------------------------
INSERT INTO `match_group` VALUES (1, 1, '第一组', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `match_group` VALUES (2, 1, '第二组', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `match_group` VALUES (3, 1, '第三组', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `match_group` VALUES (4, 1, '第四组', 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_info
-- ----------------------------
DROP TABLE IF EXISTS `match_info`;
CREATE TABLE `match_info`  (
  `mi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mi_type` int(11) DEFAULT NULL COMMENT '比赛类型：1：团队赛  0：单练',
  `mi_people_num` int(11) DEFAULT NULL COMMENT '单练同组人数',
  `mi_title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛名称',
  `mi_park_id` int(11) DEFAULT NULL COMMENT '所选球场的场地id',
  `mi_park_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所选球场的场地名称',
  `mi_zone_before_nine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '前9洞打哪个区（A/B/C）',
  `mi_zone_after_nine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '后9洞打哪个区（A/B/C）',
  `mi_digest` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛介绍',
  `mi_match_time` bigint(20) DEFAULT NULL COMMENT '比赛日期/开球时间',
  `mi_content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '比赛内容',
  `mi_match_open_type` int(11) DEFAULT NULL COMMENT '观战范围：（1、公开 球友均可见；2、队内公开：参赛者的队友可见；3、封闭：参赛队员可见）',
  `mi_join_open_type` int(255) DEFAULT NULL COMMENT '参赛范围(1、公开 球友均可报名；2、队内：某几个球队队员可报名；)',
  `mi_report_score_team_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成绩上报球队id',
  `mi_hit` int(11) DEFAULT NULL COMMENT '点击量',
  `mi_apply_end_time` bigint(20) DEFAULT NULL COMMENT '报名截止时间',
  `mi_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛发起人',
  `mi_create_user_id` bigint(20) DEFAULT NULL COMMENT '比赛发起人id',
  `mi_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `mi_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '更新人名称',
  `mi_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `mi_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`mi_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '赛事活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_info
-- ----------------------------
INSERT INTO `match_info` VALUES (1, NULL, NULL, '芒街个人挑战赛', NULL, '（18洞）Mong Cai Golf Club(越南)A场/B场', NULL, NULL, NULL, 1542245400000, '芒街高尔夫个人挑战赛', NULL, NULL, NULL, 0, 1543828610234, '张三', 1, 1543828610234, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (2, NULL, NULL, '北京公开赛2018封场杯', NULL, '北京北湖国际高尔夫球俱乐部', NULL, NULL, NULL, 1546219800000, '内容内容', NULL, NULL, NULL, 0, 1546099200000, '李四', 2, 1546099200000, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (3, NULL, NULL, '龙城队比赛', NULL, '北京华彬国际高尔夫尼克劳斯球场', NULL, NULL, '', 1545183000000, '龙城队比赛龙城队比赛龙城队比赛', NULL, NULL, NULL, 0, 1545183000000, '张三', 3, 1543828610234, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (4, NULL, NULL, '纪念金庸先生比赛', NULL, '北京东方明珠高尔夫球俱乐部', NULL, NULL, '', 1545096600000, '纪念金庸先生比赛纪念金庸先生比赛纪念金庸先生比赛', NULL, NULL, NULL, 0, 1545062400000, '张三', 4, 1543828610234, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_join_watch_info
-- ----------------------------
DROP TABLE IF EXISTS `match_join_watch_info`;
CREATE TABLE `match_join_watch_info`  (
  `mjwi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '报名表主键',
  `mjwi_type` int(11) DEFAULT NULL COMMENT '类型：0：观战 1：报名',
  `mjwi_match_id` bigint(20) DEFAULT NULL COMMENT '比赛表主键',
  `mjwi_user_id` bigint(20) DEFAULT NULL COMMENT '用户主键',
  `mjwi_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`mjwi_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '比赛报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_join_watch_info
-- ----------------------------
INSERT INTO `match_join_watch_info` VALUES (1, 1, 1, 1, NULL);
INSERT INTO `match_join_watch_info` VALUES (2, 0, 2, 2, NULL);

-- ----------------------------
-- Table structure for match_score
-- ----------------------------
DROP TABLE IF EXISTS `match_score`;
CREATE TABLE `match_score`  (
  `ms_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ms_team_id` bigint(20) DEFAULT NULL COMMENT '球队id',
  `ms_match_id` bigint(20) DEFAULT NULL COMMENT '比赛主键',
  `ms_match_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛名称',
  `ms_group_id` bigint(20) DEFAULT NULL COMMENT '比赛分组主键',
  `ms_group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组名称',
  `ms_user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `ms_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户名',
  `ms_score` int(11) DEFAULT NULL COMMENT '分数',
  `ms_hole_num` int(11) DEFAULT NULL COMMENT '球洞号（数字1-18）',
  `ms_hole_total_rod_num` int(11) DEFAULT NULL COMMENT '本洞总杆',
  `ms_push_rod_num` int(11) DEFAULT NULL COMMENT '推杆',
  `ms_is_up` int(11) DEFAULT NULL COMMENT '是否上球道（1：是  0：否）',
  `ms_match_total_rod_num` int(11) DEFAULT NULL COMMENT '本场总杆',
  `ms_create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `ms_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `ms_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `ms_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ms_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '比赛成绩表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for match_score_user_mapping
-- ----------------------------
DROP TABLE IF EXISTS `match_score_user_mapping`;
CREATE TABLE `match_score_user_mapping`  (
  `msum_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户记分mapping表主键',
  `msum_match_id` bigint(20) DEFAULT NULL COMMENT '比赛id',
  `msum_group_id` bigint(20) DEFAULT NULL COMMENT '邀请人所在分组id',
  `msum_create_user_id` bigint(20) DEFAULT NULL COMMENT '邀请人id',
  `msum_scorer_id` bigint(20) DEFAULT NULL COMMENT '记分人id',
  `msum_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`msum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户记分mapping表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_score_user_mapping
-- ----------------------------
INSERT INTO `match_score_user_mapping` VALUES (1, 1, 2, 3, 1, NULL);

-- ----------------------------
-- Table structure for match_user_group_mapping
-- ----------------------------
DROP TABLE IF EXISTS `match_user_group_mapping`;
CREATE TABLE `match_user_group_mapping`  (
  `mugm_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '比赛分组表主键',
  `mugm_match_id` bigint(20) DEFAULT NULL COMMENT '赛事id',
  `mugm_user_type` int(11) DEFAULT NULL COMMENT '用户类型：0：临时分组，1：赛长，2：普通用户',
  `mugm_group_id` bigint(11) DEFAULT NULL COMMENT '分组id',
  `mugm_group_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组名称',
  `mugm_user_id` bigint(20) DEFAULT NULL COMMENT '球友用户id',
  `mugm_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球友用户名',
  `mugm_create_user_id` bigint(20) DEFAULT NULL COMMENT '分组创建人id',
  `mugm_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组创建人姓名',
  `mugm_create_time` bigint(20) DEFAULT NULL COMMENT '分组创建时间',
  PRIMARY KEY (`mugm_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '比赛用户分组mapping记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_user_group_mapping
-- ----------------------------
INSERT INTO `match_user_group_mapping` VALUES (1, 1, 0, 1, '第一组', 1, 'nmy', 1, NULL, NULL);
INSERT INTO `match_user_group_mapping` VALUES (2, 1, 0, 2, '第二组', 2, 'zs', 1, NULL, NULL);
INSERT INTO `match_user_group_mapping` VALUES (3, 1, 1, 1, '第一组', 3, 'lisi', NULL, NULL, NULL);
INSERT INTO `match_user_group_mapping` VALUES (4, 1, 1, 2, '第二组', 4, 'wangwu', NULL, NULL, NULL);
INSERT INTO `match_user_group_mapping` VALUES (5, 1, 0, 1, '第一组', 5, 'zhaoliu', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for park_info
-- ----------------------------
DROP TABLE IF EXISTS `park_info`;
CREATE TABLE `park_info`  (
  `pi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '球场id',
  `pi_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场名称',
  `pi_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场logo',
  `pi_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场所在城市',
  `pi_is_valid` int(11) DEFAULT NULL COMMENT '是否可用(1:是  0：否)',
  `pi_lng` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '地理位置：纬度',
  `pi_lat` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '地理位置：经度',
  `pi_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `pi_create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `pi_create_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人姓名',
  `pi_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `pi_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `pi_update_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人姓名',
  PRIMARY KEY (`pi_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '球场信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for park_partition
-- ----------------------------
DROP TABLE IF EXISTS `park_partition`;
CREATE TABLE `park_partition`  (
  `pp_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分区表id',
  `pp_p_id` bigint(20) DEFAULT NULL COMMENT '场地表主键',
  `pp_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分区名称',
  `pp_hole_num` int(11) DEFAULT NULL COMMENT '球洞序号(数字 1-9)',
  `pp_hole_standard_rod` int(11) DEFAULT NULL COMMENT '球洞标准杆(数字 1-7)',
  PRIMARY KEY (`pp_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '高尔夫场地分区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of park_partition
-- ----------------------------
INSERT INTO `park_partition` VALUES (1, 1, 'A', 1, 3);
INSERT INTO `park_partition` VALUES (2, 1, 'A', 2, 4);
INSERT INTO `park_partition` VALUES (3, 1, 'B', 1, 5);
INSERT INTO `park_partition` VALUES (4, 1, 'B', 2, 3);
INSERT INTO `park_partition` VALUES (5, 1, 'C', 1, 6);

-- ----------------------------
-- Table structure for team_info
-- ----------------------------
DROP TABLE IF EXISTS `team_info`;
CREATE TABLE `team_info`  (
  `ti_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '球队id',
  `ti_logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队LOGO',
  `ti_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队名称',
  `ti_signature` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队签名',
  `ti_digest` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队简介',
  `ti_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所在地区',
  `ti_slogan` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队签名(口号)',
  `ti_join_open_type` int(11) DEFAULT NULL COMMENT '是否有加入审核(1、是（队长审批）  0、否)',
  `ti_info_open_type` int(255) DEFAULT NULL COMMENT '信息是否公开（1、公开 比赛成绩等向所有球友开放；2、封闭，比赛成绩等向队友开放）',
  `ti_match_result_audit_type` int(255) DEFAULT NULL COMMENT '比赛成绩审核（1：是  0：否）',
  `ti_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `ti_create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `ti_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人姓名',
  `ti_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `ti_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `ti_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '更新人姓名',
  PRIMARY KEY (`ti_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '球队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_info
-- ----------------------------
INSERT INTO `team_info` VALUES (1, NULL, '高尔夫球队1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `team_info` VALUES (2, NULL, '高尔夫球队2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `team_info` VALUES (3, NULL, '高尔夫球队3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for team_user_integral
-- ----------------------------
DROP TABLE IF EXISTS `team_user_integral`;
CREATE TABLE `team_user_integral`  (
  `tui_id` bigint(20) NOT NULL COMMENT '球队球友积分表主键',
  `tui_team_id` bigint(20) DEFAULT NULL COMMENT '球队主键',
  `tui_user_id` bigint(20) DEFAULT NULL COMMENT '球友主键',
  PRIMARY KEY (`tui_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '球队球友积分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team_user_mapping
-- ----------------------------
DROP TABLE IF EXISTS `team_user_mapping`;
CREATE TABLE `team_user_mapping`  (
  `tum_id` bigint(20) NOT NULL COMMENT '用户加入球队表主键',
  `tum_team_id` bigint(20) DEFAULT NULL COMMENT '球队主键',
  `tum_user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `tum_user_type` int(11) DEFAULT NULL COMMENT '用户类型（1：队长  0：队员）',
  `tum_type` int(11) DEFAULT NULL COMMENT '类型（0：待审核，1：审核通过）',
  `tum_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `tum_create_user_id` bigint(11) DEFAULT NULL COMMENT '创建人id',
  `tum_create_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人姓名',
  `tum_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `tum_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `tum_update_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人姓名',
  PRIMARY KEY (`tum_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户加入球队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_user_mapping
-- ----------------------------
INSERT INTO `team_user_mapping` VALUES (1, 1, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `team_user_mapping` VALUES (2, 3, 1, 0, 1, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `ui_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户表主键',
  `ui_type` int(11) DEFAULT NULL COMMENT '用户类型(1:理事会，2:委员会，3：普通用户)',
  `ui_headimg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户头像（同微信头像）',
  `ui_personalized_signature` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '个性签名',
  `ui_longitude` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '地理位置：经度',
  `ui_latitude` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '地理位置：纬度',
  `ui_real_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '真实姓名',
  `ui_age` int(11) DEFAULT NULL COMMENT '年龄',
  `ui_tel_no` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `ui_email` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  `ui_craduate_school` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '毕业学校',
  `ui_craduate_department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '毕业院系',
  `ui_craduate_time` bigint(20) DEFAULT NULL COMMENT '毕业时间',
  `ui_major` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '专业',
  `ui_work_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '工作单位',
  `ui_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '常住地',
  `ui_is_valid` int(11) DEFAULT NULL COMMENT '是否有效（0：否；1：是）',
  `ui_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `ui_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人姓名',
  `ui_create_user_id` bigint(11) DEFAULT NULL COMMENT '创建人id',
  `ui_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `ui_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '更新人姓名',
  `ui_update_user_id` bigint(11) DEFAULT NULL COMMENT '更新人用户id',
  PRIMARY KEY (`ui_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户详细资料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 1, NULL, NULL, NULL, NULL, 'nmy', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES (2, 1, NULL, NULL, NULL, NULL, '张三', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES (3, 1, NULL, NULL, NULL, NULL, 'lisi', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES (4, 2, NULL, NULL, NULL, NULL, 'wangwu', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user_info` VALUES (5, 3, NULL, NULL, NULL, NULL, 'zhaoliu', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for wechat_user_info
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user_info`;
CREATE TABLE `wechat_user_info`  (
  `wui_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '微信用户表主键',
  `wui_u_id` bigint(20) DEFAULT NULL COMMENT '用户表主键',
  `wui_subscribe` int(11) DEFAULT NULL COMMENT '是否订阅该公众号标识（0：未关注；1：关注）',
  `wui_openid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'openid',
  `wui_nick_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '昵称',
  `wui_real_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '真实姓名',
  `wui_phone` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `wui_sex` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '性别',
  `wui_city` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '城市',
  `wui_country` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '国家',
  `wui_province` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '省份',
  `wui_language` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '语言',
  `wui_headimgurl` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像',
  `wui_subscribe_time` bigint(20) DEFAULT NULL COMMENT '关注时间',
  `wui_unionid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'unionid',
  `wui_remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `wui_headimg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像本地路径',
  `wui_is_valid` int(11) DEFAULT NULL COMMENT '是否有效（0:否；1：是）',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `watermark_appid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `watermark_timestamp` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`wui_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '微信用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wechat_user_info
-- ----------------------------
INSERT INTO `wechat_user_info` VALUES (1, NULL, NULL, 'oXggK4xOgfkhLtHermHfY9VQsE8Q', 'null', NULL, NULL, '2', '', 'Heard Island and McDonald Islands', '', 'zh_CN', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83erSPL40G2Tgv83iclicasdWib0CiazJn9oGjPqibSadPoCdkMibSpXicEJ19icZiaOtz5H1DJ8BMDczZ7SIzgw/132', NULL, NULL, NULL, 'up/headimg\\oXggK4xOgfkhLtHermHfY9VQsE8Q.png', 1, 1544607939609, NULL, 'wx43883c89e5ed8118', '1544607938');
INSERT INTO `wechat_user_info` VALUES (2, NULL, NULL, 'oXggK4xOgfkhLtHermHfY9VQsE8Q', 'aaa', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83erSPL40G2Tgv83iclicasdWib0CiazJn9oGjPqibSadPoCdkMibSpXicEJ19icZiaOtz5H1DJ8BMDczZ7SIzgw/132', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wechat_user_info` VALUES (3, NULL, NULL, NULL, 'afdsa', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
