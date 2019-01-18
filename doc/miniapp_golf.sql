/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : miniapp_golf

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2019-01-18 16:38:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for match_info
-- ----------------------------
DROP TABLE IF EXISTS `match_info`;
CREATE TABLE `match_info` (
  `mi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mi_title` varchar(128) DEFAULT NULL COMMENT '比赛名称',
  `mi_park_id` int(11) DEFAULT NULL COMMENT '所选球场的场地id',
  `mi_park_name` varchar(128) DEFAULT NULL COMMENT '所选球场的场地名称',
  `mi_digest` varchar(512) DEFAULT NULL COMMENT '比赛介绍',
  `mi_match_time` bigint(20) DEFAULT NULL COMMENT '比赛日期/开球时间',
  `mi_content` text COMMENT '比赛内容',
  `mi_match_open_type` int(11) DEFAULT NULL COMMENT '观战范围：（1、公开 球友均可见；2、队内公开：参赛者的队友可见；3、封闭：参赛队员可见）',
  `mi_join_open_type` int(255) DEFAULT NULL COMMENT '参赛范围(1、公开 球友均可报名；2、队内：某几个球队队员可报名；)',
  `mi_report_score_type` int(11) DEFAULT NULL COMMENT '成绩上报类型：0：上报给球队，1：上报给比赛',
  `mi_hit` int(11) DEFAULT NULL COMMENT '点击量',
  `mi_apply_end_time` bigint(20) DEFAULT NULL COMMENT '报名截止时间',
  `mi_create_user_name` varchar(128) DEFAULT NULL COMMENT '比赛发起人',
  `mi_create_user_id` bigint(20) DEFAULT NULL COMMENT '比赛发起人id',
  `mi_create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `mi_update_user_name` varchar(128) DEFAULT '' COMMENT '更新人名称',
  `mi_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `mi_update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`mi_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='赛事活动表';

-- ----------------------------
-- Records of match_info
-- ----------------------------
INSERT INTO `match_info` VALUES ('1', '芒街个人挑战赛', null, '（18洞）Mong Cai Golf Club(越南)A场/B场', null, '1542245400000', '芒街高尔夫个人挑战赛', null, null, null, '0', '1543828610234', '张三', '1', '1543828610234', null, null, null);
INSERT INTO `match_info` VALUES ('2', '北京公开赛2018封场杯', null, '北京北湖国际高尔夫球俱乐部', null, '1546219800000', '内容内容', null, null, null, '0', '1546099200000', '李四', '2', '1546099200000', null, null, null);
INSERT INTO `match_info` VALUES ('3', '龙城队比赛', null, '北京华彬国际高尔夫尼克劳斯球场', '', '1545183000000', '龙城队比赛龙城队比赛龙城队比赛', null, null, null, '0', '1545183000000', '张三', '3', '1543828610234', null, null, null);
INSERT INTO `match_info` VALUES ('4', '纪念金庸先生比赛', null, '北京东方明珠高尔夫球俱乐部', '', '1545096600000', '纪念金庸先生比赛纪念金庸先生比赛纪念金庸先生比赛', null, null, null, '0', '1545062400000', '张三', '4', '1543828610234', null, null, null);
