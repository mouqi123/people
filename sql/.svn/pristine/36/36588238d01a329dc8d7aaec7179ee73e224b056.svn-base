/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : payment

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-07-12 13:55:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_account`;
CREATE TABLE `tb_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) DEFAULT NULL COMMENT '用户id',
  `account_name` varchar(255) DEFAULT NULL COMMENT '账户名称',
  `account_type` varchar(255) DEFAULT NULL COMMENT '账户类型',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_account
-- ----------------------------

-- ----------------------------
-- Table structure for tb_account_card
-- ----------------------------
DROP TABLE IF EXISTS `tb_account_card`;
CREATE TABLE `tb_account_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NOT NULL COMMENT '账户管理表id',
  `card_id` int(11) NOT NULL COMMENT '银行卡表id',
  PRIMARY KEY (`id`,`account_id`,`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_account_card
-- ----------------------------

-- ----------------------------
-- Table structure for tb_apply
-- ----------------------------
DROP TABLE IF EXISTS `tb_apply`;
CREATE TABLE `tb_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(255) DEFAULT NULL COMMENT '对应后台appcode',
  `app_key` varchar(255) DEFAULT NULL COMMENT '对应后台key',
  `app_type` varchar(11) DEFAULT NULL COMMENT '安装平台',
  `app_name` varchar(255) DEFAULT NULL COMMENT 'app名称',
  `app_version` varchar(255) DEFAULT NULL COMMENT 'app版本',
  `app_desc` varchar(255) DEFAULT NULL COMMENT 'app描述',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_apply
-- ----------------------------
INSERT INTO `tb_apply` VALUES ('2', '98434f703d5a8845abb09a17316427c54b4168a4', 'af6c72ab8c7cf72eec9f9e5e017be8c8591ed37f', '1', 'IOS', 'V1.0', 'IOS', '2016-04-13 14:03:33');
INSERT INTO `tb_apply` VALUES ('3', 'afe9391f31b52934e8ef100c62f5c08935f90eab', 'c5209e0ec6dddf7ddf9a96d1aa20a21a00fed9d', '1', 'Android', 'V1.0', 'Android', '2016-04-13 17:06:18');
INSERT INTO `tb_apply` VALUES ('4', 'b9fb07ff5b258b89f89e5f4cfe8add49c10140ac', '9bff6b82b7dd66ed951b4c9d8b27390389055bfd', '1', ' Android arm ', 'v1.0', ' Android arm ', '2016-04-13 16:56:35');
INSERT INTO `tb_apply` VALUES ('5', 'e89aabf1b8c4c1a3cb292f474004bd9227b2a2d5', '1dab8397145ad2b9483cea91c8ea475fd04c8ba9', 'IOS', 'IOS', '1.0', 'IOS', '2016-04-19 17:50:44');
INSERT INTO `tb_apply` VALUES ('8', '10f179b0bcf0a484000590beca07ff8e453ba2c7', '6e819ad0197f71fb004240331888bc98961d44a9', '1.0', 'Android arm', 'Android arm', 'Android arm', '2016-04-14 15:35:10');
INSERT INTO `tb_apply` VALUES ('9', 'edb92851b3b9f3f14f42a644d92cdaba16dcbcc2', 'afede44714ffab7892d9c0f1b89490b7b303db47', 'Android arm', '简单测试', '1.0', '简单测试', '2016-04-19 18:20:24');
INSERT INTO `tb_apply` VALUES ('10', '19da70cf8976acc0231ab362dcbc7d8df3e8749a', '6018df36bbbc23e64291f5b252a3d4807523798a', 'Android arm', 'Android arm', '2.0', 'Android arm', '2016-05-26 17:45:33');
INSERT INTO `tb_apply` VALUES ('11', '545e8d3e0ecb42144d12fc2bb0e8f8ff61368411', '45b33dd4a1eb894a5ff0f07e7097806fb1854d50', 'iOS ', 't1', '2.0', 'test', '2016-05-27 10:37:36');
INSERT INTO `tb_apply` VALUES ('19', '4f94cc2901cefd1e0c9d4f58abfba22e6c6dcaad', '629b0b166c117202d543573fa9d4c55d8de2d9d0', 'web测试', 'web测试', 'web测试', 'web测试', '2016-06-15 15:01:13');
INSERT INTO `tb_apply` VALUES ('20', '884aee78ca8eb8be6bccf96f716064691ac6b226', 'a67986a0b2bf451f45bffbbaa8d8ab66d55d31d1', 'url测试', 'url测试', 'url测试', 'url测试', '2016-06-20 17:45:13');
INSERT INTO `tb_apply` VALUES ('21', 'f5f4e117ad67f96a47305289a12badc6c2c1c325', '5d488d95a2a9d11cccc9d053c1fc01a7ffba8bdf', 'Android arm', 'Android arm', 'Android arm', 'Android arm', '2016-06-27 13:38:34');
INSERT INTO `tb_apply` VALUES ('22', '6acbe12b8ec1c848f623efc95a8398623bcfd8eb', '5e8e8ba6fa00d660002c8ebff197e81a61c72e94', 'Android arm', 'Android arm', 'Android arm', 'Android arm', '2016-06-27 14:09:17');
INSERT INTO `tb_apply` VALUES ('24', '70eeb7d7b84c73fbf4a6cc587b5314516953ba04', '2d373153c0781dbe095a806010b353f12e489068', 'Android arm', 'Android arm', 'Android arm', 'Android arm', '2016-06-27 14:35:18');
INSERT INTO `tb_apply` VALUES ('25', 'bd0ffc97cc882523ceb1e4c690d8fcb71da0cb5f', '376c5e765f5cccef9ac7860eff9c491785376b2e', 'Android arm', 'Android arm', 'Android arm', 'Android arm', '2016-06-27 14:48:31');

-- ----------------------------
-- Table structure for tb_auth
-- ----------------------------
DROP TABLE IF EXISTS `tb_auth`;
CREATE TABLE `tb_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) DEFAULT NULL COMMENT '银行卡ID',
  `login` int(11) DEFAULT NULL COMMENT '手机银行登录',
  `onepay` int(11) DEFAULT NULL COMMENT '一键支付',
  `epay` int(11) DEFAULT NULL COMMENT 'E支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_auth
-- ----------------------------
INSERT INTO `tb_auth` VALUES ('1', '1', '1', '1', '1');

-- ----------------------------
-- Table structure for tb_card
-- ----------------------------
DROP TABLE IF EXISTS `tb_card`;
CREATE TABLE `tb_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `card_number` varchar(255) DEFAULT NULL COMMENT '银行卡卡号',
  `card_pwd` varchar(255) DEFAULT NULL COMMENT '银行卡密码',
  `card_type` int(11) DEFAULT NULL COMMENT '卡类型 0：储蓄卡 1：信用卡',
  `pay_pwd` varchar(255) DEFAULT NULL COMMENT '支付密码',
  `money` varchar(255) DEFAULT NULL COMMENT '余额',
  `phone` varchar(255) DEFAULT NULL COMMENT '预留手机号',
  `name` varchar(255) DEFAULT NULL COMMENT '卡主姓名',
  `identity_type` int(11) DEFAULT NULL COMMENT '证件类型 0：身份证  1：护照 2：军官证',
  `identity_number` varchar(255) DEFAULT NULL COMMENT '证件号码',
  `one_key_status` int(11) DEFAULT '0' COMMENT '一键支付',
  `status` int(11) DEFAULT NULL COMMENT '卡状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_card
-- ----------------------------
INSERT INTO `tb_card` VALUES ('4', '6228486700131', '123456', '0', '122222', '957558', '13051126671', '俊杰', '0', '410821199206092012', '0', '0', '2016-01-20 14:22:10');

-- ----------------------------
-- Table structure for tb_deal_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_deal_log`;
CREATE TABLE `tb_deal_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deal_date` timestamp NULL DEFAULT NULL COMMENT '操作日期',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机哈',
  `deal_info` varchar(2000) DEFAULT NULL COMMENT '操作的信息',
  `pc_info` varchar(2000) DEFAULT NULL COMMENT '电脑返回信息',
  `status` int(11) DEFAULT NULL,
  `data` varchar(2000) DEFAULT NULL COMMENT '报文',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=269 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_deal_log
-- ----------------------------

-- ----------------------------
-- Table structure for tb_deal_tactics
-- ----------------------------
DROP TABLE IF EXISTS `tb_deal_tactics`;
CREATE TABLE `tb_deal_tactics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(255) DEFAULT NULL COMMENT '用户id',
  `last_money` varchar(255) DEFAULT NULL COMMENT '支付策略的金额',
  `last_time` varchar(255) DEFAULT NULL COMMENT '支付策略的时间间隔',
  `pay_money` varchar(255) DEFAULT NULL COMMENT '定义支付金额是否需要认证',
  `limit_money` varchar(255) DEFAULT NULL COMMENT '消费金额',
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_deal_tactics
-- ----------------------------
INSERT INTO `tb_deal_tactics` VALUES ('1', '1', '12332', '123', '123', '100000000', '2016-01-20 13:36:07');

-- ----------------------------
-- Table structure for tb_deal_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_deal_type`;
CREATE TABLE `tb_deal_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) DEFAULT NULL COMMENT '类型名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_deal_type
-- ----------------------------

-- ----------------------------
-- Table structure for tb_holdinfo
-- ----------------------------
DROP TABLE IF EXISTS `tb_holdinfo`;
CREATE TABLE `tb_holdinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) DEFAULT NULL,
  `holdinfo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_holdinfo
-- ----------------------------
INSERT INTO `tb_holdinfo` VALUES ('208', '13051126671', 'shsh');

-- ----------------------------
-- Table structure for tb_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_member`;
CREATE TABLE `tb_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户账户',
  `user_pwd` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `identity_type` int(255) DEFAULT NULL COMMENT '证件类型 0：身份证  1：护照 2：军官证',
  `identity_number` varchar(255) DEFAULT NULL COMMENT '证件号码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` int(11) DEFAULT NULL COMMENT '用户状态',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `login_time` varchar(20) DEFAULT NULL COMMENT '登录时间',
  `plugin_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_member
-- ----------------------------
INSERT INTO `tb_member` VALUES ('1', '刘鑫鑫', '123456', '0', '410821199206092012', '13051126671', null, '2016-01-15 17:22:59', '2016-07-05 14:43:32', '14461313474556860000000005');

-- ----------------------------
-- Table structure for tb_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_order`;
CREATE TABLE `tb_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '0:未支付   1：已支付',
  `member_id` int(11) DEFAULT NULL COMMENT '用户id',
  `order_number` varchar(255) DEFAULT NULL COMMENT '订单号',
  `order_money` varchar(255) DEFAULT '' COMMENT '订单金额',
  `order_start` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `order_end` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单结束时间',
  `order_status` int(11) DEFAULT NULL COMMENT '订单状态  0:已支付 1：未支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_order
-- ----------------------------
INSERT INTO `tb_order` VALUES ('1', '1', '201233344', '179.5', '2016-03-08 17:58:50', '2016-03-08 17:58:50', '0');

-- ----------------------------
-- Table structure for tb_session_key
-- ----------------------------
DROP TABLE IF EXISTS `tb_session_key`;
CREATE TABLE `tb_session_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sotp_id` varchar(255) DEFAULT NULL,
  `randa` varchar(255) DEFAULT NULL,
  `randb` varchar(255) DEFAULT NULL,
  `session_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_session_key
-- ----------------------------

-- ----------------------------
-- Table structure for tb_sotpauth
-- ----------------------------
DROP TABLE IF EXISTS `tb_sotpauth`;
CREATE TABLE `tb_sotpauth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL COMMENT '服务器IP地址',
  `post` varchar(255) DEFAULT NULL COMMENT '服务器端口',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_sotpauth
-- ----------------------------
INSERT INTO `tb_sotpauth` VALUES ('1', '192.168.20.7', '8080');

-- ----------------------------
-- Table structure for t_system_auditlog
-- ----------------------------
DROP TABLE IF EXISTS `t_system_auditlog`;
CREATE TABLE `t_system_auditlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `modelname` varchar(128) DEFAULT NULL COMMENT '模块名称',
  `masterid` bigint(20) DEFAULT NULL COMMENT '操作人编号',
  `srcip` varchar(32) DEFAULT NULL,
  `serverip` varchar(32) DEFAULT NULL COMMENT '服务器ip',
  `serverport` int(11) DEFAULT NULL COMMENT '服务器端口',
  `operation` varchar(128) DEFAULT NULL COMMENT '操作类型',
  `opttime` datetime DEFAULT NULL COMMENT '操作时间',
  `levelnum` int(11) DEFAULT NULL COMMENT '日志级别',
  `status` int(11) DEFAULT NULL COMMENT '操作结果1=操作成功2=操作失败',
  `description` varchar(1024) DEFAULT NULL COMMENT '操作描述:账号[账号名称]操作类型{模块名称[主键ID或唯一标识][操作内容（添加、修改前后）]}{失败原因}',
  `logtype` int(11) DEFAULT NULL COMMENT '日志类别  0：业务操作 1：交易操作',
  `data` varchar(8000) DEFAULT NULL COMMENT '数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=507 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_system_auditlog
-- ----------------------------

-- ----------------------------
-- Table structure for t_system_user
-- ----------------------------
DROP TABLE IF EXISTS `t_system_user`;
CREATE TABLE `t_system_user` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `realname` varchar(64) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `mobile` varchar(11) DEFAULT NULL,
  `roleId` varchar(64) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_user` varchar(64) DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `modify_user` varchar(64) DEFAULT NULL,
  `modify_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `login_fail_count` int(11) DEFAULT NULL COMMENT '系统用户连续登录失败次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_system_user
-- ----------------------------
INSERT INTO `t_system_user` VALUES ('-1', 'admin', '超级管理员', 'efd1c61d4cdc7c7f', 'sdfsd@163.com', '13051126671', '0', '1', 'admin', '2016-03-08 17:46:34', 'admin', '2016-03-08 17:46:34', '2');
