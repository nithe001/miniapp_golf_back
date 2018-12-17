/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : miniapp_golf

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2018-12-14 18:10:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `au_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '后台用户表主键',
  `au_user_name` varchar(128) DEFAULT NULL COMMENT '用户名',
  `au_password` varchar(128) DEFAULT NULL COMMENT '密码',
  `au_show_name` varchar(128) DEFAULT NULL COMMENT '姓名',
  `au_sex` int(11) DEFAULT NULL COMMENT '性别',
  `au_age` int(11) DEFAULT NULL COMMENT '年龄',
  `au_tel` varchar(128) DEFAULT NULL COMMENT '联系电话',
  `au_email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `au_role` bigint(20) DEFAULT NULL COMMENT '角色',
  `au_is_valid` int(11) DEFAULT NULL COMMENT '是否有效',
  `au_create_date` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `au_create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `au_create_user_name` varchar(128) DEFAULT NULL COMMENT '创建人姓名',
  `au_update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人id',
  `au_update_user_name` varchar(128) DEFAULT NULL COMMENT '更新人名称',
  `au_update_date` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`au_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='后台用户表';

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES ('1', 'peihong', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '张三', '1', '20', null, '', '1', '1', '1', '1', '1', null, null, null);
INSERT INTO `admin_user` VALUES ('2', 'admin', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', '123', null, null, null, '', null, '1', '1494236192458', '1', 'peihong', null, null, null);
INSERT INTO `admin_user` VALUES ('3', 'nmy', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'nmy', null, null, null, null, '1', '1', null, null, null, null, null, null);

-- ----------------------------
-- Table structure for match_info
-- ----------------------------
DROP TABLE IF EXISTS `match_info`;
CREATE TABLE `match_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `match_time` bigint(20) DEFAULT NULL COMMENT '比赛日期',
  `title` varchar(128) DEFAULT NULL COMMENT '活动主题',
  `digest` varchar(512) DEFAULT NULL COMMENT '比赛介绍',
  `thumb` varchar(128) DEFAULT NULL COMMENT '缩略图路径',
  `content` text COMMENT '比赛内容',
  `address` varchar(128) DEFAULT NULL COMMENT '地点',
  `is_del` int(11) DEFAULT NULL COMMENT '是否删除（1：是，0：否）',
  `is_open` int(11) DEFAULT NULL COMMENT '是否完全公开（1：是，0：否）',
  `state` int(11) DEFAULT NULL COMMENT '状态(1:进行中，0：结束)',
  `hit` int(11) DEFAULT NULL COMMENT '点击量',
  `create_user_name` varchar(128) DEFAULT NULL COMMENT '比赛发起人',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '比赛发起人id',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `apply_start_time` bigint(20) DEFAULT NULL COMMENT '报名开始时间',
  `apply_end_time` bigint(20) DEFAULT NULL COMMENT '报名截止时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='赛事活动表';

-- ----------------------------
-- Records of match_info
-- ----------------------------
INSERT INTO `match_info` VALUES ('1', '1543828610234', '芒街个人挑战赛', null, null, '芒街高尔夫个人挑战赛', '（18洞）Mong Cai Golf Club(越南)A场/B场', null, null, '1', null, '张三', '1', '1543828610234', '1543828610234', '1543828610234');

-- ----------------------------
-- Table structure for team_info
-- ----------------------------
DROP TABLE IF EXISTS `team_info`;
CREATE TABLE `team_info` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='球队表';

-- ----------------------------
-- Records of team_info
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户表主键',
  `type` int(11) DEFAULT NULL COMMENT '用户类型(1:理事会，2:委员会，3：普通用户)',
  `real_name` varchar(128) DEFAULT NULL COMMENT '真实姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `tel_no` varchar(128) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `club` bigint(20) DEFAULT NULL COMMENT '所属俱乐部的id',
  `is_valid` int(11) DEFAULT '1' COMMENT '是否有效（0：否；1：是）',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user_info
-- ----------------------------

-- ----------------------------
-- Table structure for wechat_user_info
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user_info`;
CREATE TABLE `wechat_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '微信用户表主键',
  `u_id` bigint(20) DEFAULT NULL COMMENT '用户表主键',
  `subscribe` int(11) DEFAULT NULL COMMENT '是否订阅该公众号标识（0：未关注；1：关注）',
  `openid` varchar(128) DEFAULT NULL COMMENT 'openid',
  `nick_name` varchar(128) DEFAULT NULL COMMENT '昵称',
  `sex` varchar(128) DEFAULT NULL COMMENT '性别',
  `city` varchar(128) DEFAULT NULL COMMENT '城市',
  `country` varchar(128) DEFAULT NULL COMMENT '国家',
  `province` varchar(128) DEFAULT NULL COMMENT '省份',
  `language` varchar(128) DEFAULT NULL COMMENT '语言',
  `headimgurl` varchar(512) DEFAULT NULL COMMENT '头像',
  `subscribe_time` bigint(20) DEFAULT NULL COMMENT '关注时间',
  `unionid` varchar(128) DEFAULT NULL COMMENT 'unionid',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `headimg` varchar(128) DEFAULT NULL COMMENT '头像本地路径',
  `is_valid` int(11) DEFAULT NULL COMMENT '是否有效（0:否；1：是）',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `watermark_appid` varchar(128) DEFAULT NULL,
  `watermark_timestamp` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='微信用户表';

-- ----------------------------
-- Records of wechat_user_info
-- ----------------------------
INSERT INTO `wechat_user_info` VALUES ('1', null, null, 'oXggK4xOgfkhLtHermHfY9VQsE8Q', 'null', '2', '', 'Heard Island and McDonald Islands', '', 'zh_CN', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83erSPL40G2Tgv83iclicasdWib0CiazJn9oGjPqibSadPoCdkMibSpXicEJ19icZiaOtz5H1DJ8BMDczZ7SIzgw/132', null, null, null, 'up/headimg\\oXggK4xOgfkhLtHermHfY9VQsE8Q.png', '1', '1544607939609', null, 'wx43883c89e5ed8118', '1544607938');
