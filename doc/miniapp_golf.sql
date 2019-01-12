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

 Date: 12/01/2019 23:44:10
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '后台用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'peihong', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '张三', 1, 20, NULL, '', 1, 1, 1, 1, '1', NULL, NULL, NULL);
INSERT INTO `admin_user` VALUES (2, 'admin', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '123', NULL, NULL, NULL, '', NULL, 1, 1494236192458, 1, 'peihong', NULL, NULL, NULL);
INSERT INTO `admin_user` VALUES (3, 'nmy', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'nmy', NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_group_info
-- ----------------------------
DROP TABLE IF EXISTS `match_group_info`;
CREATE TABLE `match_group_info`  (
  `mgi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '比赛分组表主键',
  `mgi_match_id` bigint(20) DEFAULT NULL COMMENT '赛事id',
  `mgi_group_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组名称',
  `mgi_user_id` bigint(20) DEFAULT NULL COMMENT '球友用户id',
  `mgi_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球友用户名',
  `mgi_create_user_id` bigint(20) DEFAULT NULL COMMENT '分组创建人id',
  `mgi_create_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组创建人姓名',
  `mgi_create_time` bigint(20) DEFAULT NULL COMMENT '分组创建时间',
  `mgi_update_user_id` bigint(11) DEFAULT NULL COMMENT '分组更新人id',
  `mgi_update_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组更新人姓名',
  `mgi_update_time` bigint(20) DEFAULT NULL COMMENT '分组更新时间',
  PRIMARY KEY (`mgi_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '比赛分组记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_group_info
-- ----------------------------
INSERT INTO `match_group_info` VALUES (1, 1, '第一组', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `match_group_info` VALUES (2, 1, '第二组', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_info
-- ----------------------------
DROP TABLE IF EXISTS `match_info`;
CREATE TABLE `match_info`  (
  `mi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mi_title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛名称',
  `mi_park_id` int(11) DEFAULT NULL COMMENT '所选球场的场地id',
  `mi_park_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所选球场的场地名称',
  `mi_digest` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛介绍',
  `mi_match_time` bigint(20) DEFAULT NULL COMMENT '比赛日期/开球时间',
  `mi_content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '比赛内容',
  `mi_match_open_type` int(11) DEFAULT NULL COMMENT '观战范围：（1、公开 球友均可见；2、队内公开：参赛者的队友可见；3、封闭：参赛队员可见）',
  `mi_join_open_type` int(255) DEFAULT NULL COMMENT '参赛范围(1、公开 球友均可报名；2、队内：某几个球队队员可报名；)',
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
INSERT INTO `match_info` VALUES (1, '芒街个人挑战赛', NULL, '（18洞）Mong Cai Golf Club(越南)A场/B场', NULL, 1542245400000, '芒街高尔夫个人挑战赛', NULL, NULL, 0, 1543828610234, '张三', 1, 1543828610234, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (2, '北京公开赛2018封场杯', NULL, '北京北湖国际高尔夫球俱乐部', NULL, 1546219800000, '内容内容', NULL, NULL, 0, 1546099200000, '李四', NULL, 1546099200000, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (3, '龙城队比赛', NULL, '北京华彬国际高尔夫尼克劳斯球场', '', 1545183000000, '龙城队比赛龙城队比赛龙城队比赛', NULL, NULL, 0, 1545183000000, '张三', 1, 1543828610234, NULL, NULL, NULL);
INSERT INTO `match_info` VALUES (4, '纪念金庸先生比赛', NULL, '北京东方明珠高尔夫球俱乐部', '', 1545096600000, '纪念金庸先生比赛纪念金庸先生比赛纪念金庸先生比赛', NULL, NULL, 0, 1545062400000, '张三', 1, 1543828610234, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for match_score
-- ----------------------------
DROP TABLE IF EXISTS `match_score`;
CREATE TABLE `match_score`  (
  `ms_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ms_match_id` bigint(11) DEFAULT NULL COMMENT '比赛主键',
  `ms_match_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '比赛名称',
  `ms_group_id` bigint(11) DEFAULT NULL COMMENT '比赛分组主键',
  `ms_group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '分组名称',
  `ms_user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `ms_user_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户名',
  `ms_score` int(11) DEFAULT NULL COMMENT '分数',
  `ms_hole_num` int(11) DEFAULT NULL COMMENT '球洞号（数字1-18）',
  `ms_hole_total_rod_num` int(11) DEFAULT NULL COMMENT '本洞总杆',
  `ms_push_rod_num` int(11) DEFAULT NULL COMMENT '推杆',
  `ms_is_up` int(11) DEFAULT NULL COMMENT '是否上球道（1：是  0：否）',
  `ms_match_total_rod_num` int(11) DEFAULT NULL COMMENT '本场总杆',
  `ms_create_user_id` bigint(11) DEFAULT NULL COMMENT '创建人id',
  `ms_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `ms_update_user_id` bigint(11) DEFAULT NULL COMMENT '更新人id',
  `ms_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ms_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '比赛成绩表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for park_info
-- ----------------------------
DROP TABLE IF EXISTS `park_info`;
CREATE TABLE `park_info`  (
  `pi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '球场id',
  `pi_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场名称',
  `pi_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场logo',
  `pi_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '球场地理位置（用经纬度标识）',
  `pi_is_valid` int(11) DEFAULT NULL COMMENT '是否可用(1:是  0：否)',
  `pi_create_time` bigint(20) DEFAULT NULL COMMENT ' 创建时间',
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
  `pp_hole_t_black_distance` int(11) DEFAULT NULL COMMENT '黑球T距离',
  `pp_hole_t_gold_distance` int(11) DEFAULT NULL COMMENT '金球T距离',
  `pp_hole_t_blue_distance` int(11) DEFAULT NULL COMMENT '蓝球T距离',
  `pp_hole_t_white_distance` int(11) DEFAULT NULL COMMENT '白球T距离',
  `pp_hole_t_red_distance` int(11) DEFAULT NULL COMMENT '红球T距离',
  PRIMARY KEY (`pp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '高尔夫场地分区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team_info
-- ----------------------------
DROP TABLE IF EXISTS `team_info`;
CREATE TABLE `team_info`  (
  `ti_team_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '球队id',
  `ti_team_logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队LOGO',
  `ti_team_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队名称',
  `ti_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所在地区',
  `ti_slogan` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球队签名(口号)',
  `ti_join_open_type` int(11) DEFAULT NULL COMMENT '报名限制(1、公开 球友均可加入；2、审核：队长批准后加入)',
  `ti_info_open_type` int(255) DEFAULT NULL COMMENT '信息是否公开（1、公开 比赛成绩等向所有球友开放；2、封闭，比赛成绩等向队友开放）',
  `ti_match_result_audit_type` int(255) DEFAULT NULL COMMENT '是否比赛成绩审核（1：是  0：否）',
  PRIMARY KEY (`ti_team_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '球队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `ui_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户表主键',
  `ui_type` int(11) DEFAULT NULL COMMENT '用户类型(1:理事会，2:委员会，3：普通用户)',
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
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户详细资料表' ROW_FORMAT = Dynamic;

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
