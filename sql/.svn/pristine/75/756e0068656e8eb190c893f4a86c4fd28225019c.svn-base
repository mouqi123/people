/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : autoslyapp

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-07-12 14:28:25
*/

SET FOREIGN_KEY_CHECKS=0;

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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_apply
-- ----------------------------
INSERT INTO `tb_apply` VALUES ('2', '98434f703d5a8845abb09a17316427c54b4168a4', 'af6c72ab8c7cf72eec9f9e5e017be8c8591ed37f', '1', 'IOS', 'V1.0', 'IOS', '2016-04-13 14:03:33');
INSERT INTO `tb_apply` VALUES ('3', 'afe9391f31b52934e8ef100c62f5c08935f90eab', 'c5209e0ec6dddf7ddf9a96d1aa20a21a00fed9d', '1', 'Android', 'V1.0', 'Android', '2016-04-13 17:06:18');
INSERT INTO `tb_apply` VALUES ('4', 'b9fb07ff5b258b89f89e5f4cfe8add49c10140ac', '9bff6b82b7dd66ed951b4c9d8b27390389055bfd', '1', ' Android arm ', 'v1.0', ' Android arm ', '2016-04-13 16:56:35');
INSERT INTO `tb_apply` VALUES ('5', 'e89aabf1b8c4c1a3cb292f474004bd9227b2a2d5', '1dab8397145ad2b9483cea91c8ea475fd04c8ba9', 'IOS', 'IOS', '1.0', 'IOS', '2016-04-13 20:13:29');
INSERT INTO `tb_apply` VALUES ('8', '10f179b0bcf0a484000590beca07ff8e453ba2c7', '6e819ad0197f71fb004240331888bc98961d44a9', '1.0', 'Android arm', 'Android arm', 'Android arm', '2016-04-14 15:35:10');
INSERT INTO `tb_apply` VALUES ('9', 'a0d5d02a112d0b77360db0814d2baad45a6a993d', 'fae6e2c6bcc8e6c7720e36c2a978dbf029d5a19a', 'Android arm', 'Android arm', '1.2', '东方测试后修复版', '2016-04-20 14:48:16');
INSERT INTO `tb_apply` VALUES ('10', '884aee78ca8eb8be6bccf96f716064691ac6b226', 'a67986a0b2bf451f45bffbbaa8d8ab66d55d31d1', 'Android arm', '工行测试', '1.3', '东方测试后再测试修改版本', '2016-04-25 19:10:26');
INSERT INTO `tb_apply` VALUES ('11', 'a18f5f3660d1350fd69fea63a16a9467baffe0e3', '6f5ec92cd2a9191ed49620322c3426a9489773a7', 'IOS', 'ios211', '1.0.0', '211ios1.0', '2016-04-27 13:29:11');
INSERT INTO `tb_apply` VALUES ('12', 'c8d343fe399b9da5fbe4b569f46bc3304f71ee51', 'b2c30f64a17d4533d360afccd5afaafc62be3fa2', 'iOS', '测试', '1.0.1', 'test', '2016-04-29 15:38:46');
INSERT INTO `tb_apply` VALUES ('13', '7f56af4af1cfcb4b51330fbaa55edb83e4bf57cb', '767e9b62f0e2dce587632e6bcbcaefcc80a21b09', 'ios ', 'test', 'lua_2.0', 'lua', '2016-05-09 14:11:45');
INSERT INTO `tb_apply` VALUES ('14', '5aad923866d61b8816c509de6c4a25b1e70c001c', 'e35e106107a7b3ec69911d8df12f1b152c88e63e', 'Android arm', '密码机测试', 'Android arm', '密码机测试', '2016-05-11 10:50:43');
INSERT INTO `tb_apply` VALUES ('15', 'd5c6170a42e806b29a420def9e43ddf446e908b3', '20ff3fcb4ea9c22679fe736720cf2f3e465e0d53', 'test', 'test', '211test', 'test', '2016-05-11 13:34:03');
INSERT INTO `tb_apply` VALUES ('16', '25e5366b3c6047ea2d3662d71511bcb95e99d587', '402a633f8f6710bc3ae5c84e2d8bffef4b74a419', '211:8380', '211', '211', '211', '2016-05-11 14:07:53');
INSERT INTO `tb_apply` VALUES ('17', 'a1fb80914628c8f9958fae51946bec01bace49cc', 'aa0887402efb845bd5767150387ea5119ca807fe', 'android arm', 'android arm', 'android arm', 'android arm', '2016-05-27 14:37:55');
INSERT INTO `tb_apply` VALUES ('18', '545e8d3e0ecb42144d12fc2bb0e8f8ff61368411', '45b33dd4a1eb894a5ff0f07e7097806fb1854d50', 'web测试', 'web测试', 'web测试', 'web测试', '2016-06-13 17:02:38');
INSERT INTO `tb_apply` VALUES ('19', '4f94cc2901cefd1e0c9d4f58abfba22e6c6dcaad', '629b0b166c117202d543573fa9d4c55d8de2d9d0', 'web测试', 'web测试', 'web测试', 'web测试', '2016-06-15 15:01:13');
INSERT INTO `tb_apply` VALUES ('20', '28918a48669c52a6abcb5fb231d96a1c27b89bf6', 'ad2c0361dd836432b6520c7daff139150576090b', 'android arm', 'android arm', 'android arm', 'android arm', '2016-06-08 15:46:34');
INSERT INTO `tb_apply` VALUES ('21', '55a58c1c5929128acd247dc4feea36ddbcc26359', 'b89717c0c6056c79fffac6b943170068123b7bf3', 'android arm', 'android arm', 'android arm', 'android arm', '2016-06-22 14:19:19');
INSERT INTO `tb_apply` VALUES ('22', '8fc7b88ed26642bffefa528f1d82aff5fd5cbaf4', '2e0781caee5a7ef67a6863209d09ab7272dc9b18', 'ios', 'ios', 'ios', 'ios', '2016-06-28 15:50:32');
INSERT INTO `tb_apply` VALUES ('23', 'dd007ad308f56ac07177fb3c7aaec2add65c4418', '7f3593402baf3e6c4b94b469587acedd05983e09', 'ios', 'ios', 'ios', 'ios', '2016-06-28 15:50:31');
INSERT INTO `tb_apply` VALUES ('24', '3a6ab52c1e28ed4d3755d866afc751819550f9ee', '7d6365339c2ae07cac8a5790f8c94910299fbb55', 'ios ', 'sdk', 'sdk3.1', 'test', '2016-06-29 14:39:23');

-- ----------------------------
-- Table structure for tb_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_member`;
CREATE TABLE `tb_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户账户',
  `password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `identity_type` int(255) DEFAULT NULL COMMENT '证件类型 0：身份证  1：护照 2：军官证',
  `identity_number` varchar(255) DEFAULT NULL COMMENT '证件号码',
  `phoneNum` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` int(11) DEFAULT NULL COMMENT '用户状态',
  `create_time` varchar(20) DEFAULT NULL COMMENT '登录时间',
  `email` varchar(255) DEFAULT NULL,
  `pic_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_member
-- ----------------------------
INSERT INTO `tb_member` VALUES ('105', 'didn\'t inch dBhhh', '11', null, null, '11', null, '2016-06-22 09:52:02', null, '');
INSERT INTO `tb_member` VALUES ('108', null, '123', null, null, '123', null, '2016-06-22 14:17:40', null, '/download/108/123.png');
INSERT INTO `tb_member` VALUES ('109', null, '1', null, null, '1234', null, '2016-06-24 14:40:49', null, null);
INSERT INTO `tb_member` VALUES ('110', null, '1', null, null, '12345', null, '2016-06-24 15:49:10', null, null);
INSERT INTO `tb_member` VALUES ('111', null, '1', null, null, '123456', null, '2016-06-24 16:37:32', null, null);
INSERT INTO `tb_member` VALUES ('112', null, '1', null, null, '1234567', null, '2016-06-24 17:29:10', null, null);
INSERT INTO `tb_member` VALUES ('113', null, '1', null, null, '12345678', null, '2016-06-24 17:51:42', null, null);
INSERT INTO `tb_member` VALUES ('114', null, 'q', null, null, '1231', null, '2016-07-01 11:02:21', null, null);

-- ----------------------------
-- Table structure for tb_sotpauth
-- ----------------------------
DROP TABLE IF EXISTS `tb_sotpauth`;
CREATE TABLE `tb_sotpauth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL COMMENT '服务器IP地址',
  `post` varchar(255) DEFAULT NULL COMMENT '服务器端口',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_sotpauth
-- ----------------------------
INSERT INTO `tb_sotpauth` VALUES ('1', '192.168.20.7', '8080');

-- ----------------------------
-- Table structure for t_app_loginlog
-- ----------------------------
DROP TABLE IF EXISTS `t_app_loginlog`;
CREATE TABLE `t_app_loginlog` (
  `phone_num` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `login_time` varchar(255) DEFAULT NULL,
  `phone_type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_app_loginlog
-- ----------------------------
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 10:48:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 15:30:45', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 15:30:45', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1', '北京市', '2016-06-13 15:30:45', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 09:49:39', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 09:57:35', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:04:06', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:08:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:09:03', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:16:23', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:20:27', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:43:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:43:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:48:17', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 10:49:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 11:23:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 15:31:03', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 15:58:23', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 16:03:47', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-22 16:05:56', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 15:00:58', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 16:26:22', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 16:52:14', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 16:55:01', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 16:56:43', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 16:58:43', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:00:39', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:04:21', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:22:26', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:26:41', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:28:00', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:33:28', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:36:28', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 17:59:22', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:06:20', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:07:46', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:15:45', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:16:52', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:19:42', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:21:16', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:22:33', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:25:03', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:26:02', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:27:03', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:29:27', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-23 18:32:32', 'SM-G9008V');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 11:15:54', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:12:53', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:23:53', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:27:12', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:28:30', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:31:31', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-24 14:38:32', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 14:40:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 14:44:19', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 14:52:16', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 14:57:54', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:02:07', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:31:44', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:31:44', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:41:22', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:41:22', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:42:33', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:42:33', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:42:33', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 15:47:15', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 15:49:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 15:54:10', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 16:03:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 16:03:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 16:07:26', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345', '北京市', '2016-06-24 16:27:05', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 16:35:54', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-06-24 16:35:54', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('123456', '北京市', '2016-06-24 16:37:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('123456', '北京市', '2016-06-24 16:51:55', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('123456', '北京市', '2016-06-24 16:51:55', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '未知位置', '2016-06-27 11:20:03', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-27 11:24:29', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 14:09:52', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 15:41:14', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 15:41:14', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 15:41:14', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 15:57:08', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:03:38', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:06:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:08:11', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:08:11', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:33:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:49:02', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:49:29', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:50:40', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-28 16:52:19', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 13:42:35', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 13:45:45', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:09:10', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:12:19', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:12:19', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:15:09', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:15:09', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:15:09', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:49:44', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:49:44', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 14:56:09', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:03:23', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:13:35', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:18:32', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:21:42', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:23:33', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:26:00', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '未知位置', '2016-06-29 15:29:31', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:32:39', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:36:20', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:37:39', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:38:17', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:40:18', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:42:23', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:43:59', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:45:57', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:49:16', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:50:29', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:54:11', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 15:57:35', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:00:15', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:03:44', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:04:57', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:06:35', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:11:01', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:16:02', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:19:53', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:26:00', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:29:50', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:32:15', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:35:27', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:39:30', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:41:27', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:46:10', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:46:39', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:49:38', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:51:26', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 16:59:07', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:01:38', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:03:59', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:07:56', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:17:45', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:19:02', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:19:15', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:32:35', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:39:03', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:41:56', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:44:09', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:46:18', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-29 17:53:31', 'HUAWEI P8max');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:05:09', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:05:50', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:07:23', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:08:23', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:18:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:51:24', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 14:59:54', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:06:29', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:06:39', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:27:06', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:28:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:50:20', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:50:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:52:00', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:55:49', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:55:49', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 15:59:48', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:03:00', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:06:47', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:13:26', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:15:41', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:19:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:42:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:44:53', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:48:59', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 16:52:39', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:03:31', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:07:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:07:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:09:17', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:09:17', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:15:49', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 17:59:14', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 18:02:01', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 18:24:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:26:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:26:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:26:18', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:30:04', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:34:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:40:37', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:43:36', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:46:02', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:48:21', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:56:58', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:59:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 19:59:25', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:02:50', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:06:55', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:12:23', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:15:19', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:17:02', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:19:17', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:20:30', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:22:56', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:25:46', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-06-30 20:27:48', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 09:59:47', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 10:01:39', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 10:23:56', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 10:26:51', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 10:28:48', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-01 10:41:32', 'Nexus 5');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('1231', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('12345678', '北京市', '2016-07-01 11:00:58', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('1231', '北京市', '2016-07-01 11:07:06', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('123', '中国北京市海淀区四季青镇北坞村路19号', '2016-07-04 17:49:00', 'iPhone 5s (A1457/A1518/A1528/A1530)');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-05 10:52:39', 'HUAWEI GRA-CL10');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-05 10:53:53', 'HUAWEI GRA-CL10');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-05 10:52:39', 'HUAWEI GRA-CL10');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-05 11:24:43', 'HUAWEI GRA-CL10');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-05 11:24:43', 'HUAWEI GRA-CL10');
INSERT INTO `t_app_loginlog` VALUES ('123', '北京市', '2016-07-05 11:39:00', 'Coolpad 7620L');
INSERT INTO `t_app_loginlog` VALUES ('11', '北京市', '2016-07-06 11:22:26', 'N5207');
INSERT INTO `t_app_loginlog` VALUES ('1234', '北京市', '2016-07-07 17:59:15', 'Coolpad 7620L');

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
) ENGINE=InnoDB AUTO_INCREMENT=627 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_system_auditlog
-- ----------------------------
INSERT INTO `t_system_auditlog` VALUES ('362', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 15:58:05', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('363', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 16:49:33', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('364', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:00:06', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('365', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:01:16', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('366', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:03:04', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('367', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:08:16', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('368', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:15:03', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('369', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:15:39', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('370', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:16:52', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('371', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:20:10', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('372', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:28:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('373', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:29:04', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('374', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:30:48', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('375', '认证模块', '-1', '127.0.0.1', '192.168.1.168', '8080', '认证', '2016-03-10 17:36:35', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('376', '认证模块', '-1', '192.168.1.135', '192.168.1.168', '8080', '认证', '2016-03-10 17:47:12', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('377', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 14:04:34', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('378', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-14 14:05:18', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('379', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-14 14:19:49', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('380', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-14 14:43:19', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('381', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 14:47:15', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('382', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 16:15:56', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('383', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 16:19:00', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('384', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 16:20:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('385', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-14 16:32:19', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('386', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 16:54:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('387', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 17:30:44', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('388', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 17:45:32', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('389', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-14 17:57:45', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('390', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 18:25:31', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('391', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 18:35:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('392', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 18:41:09', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('393', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 19:09:40', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('394', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 19:18:20', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('395', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 19:29:46', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('396', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 19:30:34', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('397', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-14 19:39:44', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('398', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 09:43:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('399', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 10:01:02', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('400', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-15 10:02:02', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('401', '认证模块', '-1', '192.168.1.146', '192.168.126.1', '8080', '认证', '2016-03-15 10:16:33', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('402', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 10:47:32', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('403', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 14:00:23', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('404', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 14:10:12', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('405', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-15 14:18:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('406', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-15 14:21:15', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('407', '认证模块', '-1', '192.168.1.175', '192.168.126.1', '8080', '认证', '2016-03-15 14:22:26', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('408', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-28 17:37:31', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('409', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 10:59:50', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('410', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 11:18:30', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('411', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 16:30:10', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('412', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 16:40:22', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('413', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 16:47:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('414', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 17:00:21', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('415', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 17:52:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('416', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 17:57:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('417', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-29 18:01:28', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('418', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 10:51:57', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('419', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:01:29', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('420', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:03:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('421', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:04:50', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('422', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:16:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('423', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:27:17', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('424', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 11:31:11', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('425', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 13:49:00', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('426', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 13:58:47', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('427', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 13:59:33', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('428', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 14:03:42', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('429', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 14:23:35', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('430', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-03-30 16:04:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('431', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 11:14:23', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('432', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 14:20:25', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('433', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 15:53:04', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('434', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 16:02:03', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('435', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 16:27:50', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('436', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 16:32:29', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('437', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:22:49', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('438', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:30:17', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('439', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:33:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('440', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:39:38', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('441', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:39:55', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('442', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:44:43', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('443', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 17:58:07', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('444', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 18:16:15', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('445', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-07 18:21:55', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('446', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-08 17:39:16', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('447', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-11 10:09:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('448', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-13 10:50:02', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('449', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 10:51:41', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('450', '认证模块', '0', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-13 10:53:25', '3', '2', '账号[shenji]登录失败,失败原因:{用户[shenji]不存在！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('451', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-13 10:53:34', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('452', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-13 10:54:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('453', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 11:28:44', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('454', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-13 13:47:46', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('455', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 14:01:24', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('456', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-13 14:24:14', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('457', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 16:03:05', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('458', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-13 16:52:18', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('459', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 16:54:01', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('460', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-13 21:08:51', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('461', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-14 09:39:28', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('462', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-14 10:34:41', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('463', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-14 11:24:56', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('464', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8380', '认证', '2016-04-15 17:20:21', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('465', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8380', '认证', '2016-04-15 18:08:50', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('466', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8380', '认证', '2016-04-15 18:22:47', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('467', '认证模块', '-1', '192.168.1.147', '127.0.0.1', '8380', '认证', '2016-04-15 18:23:53', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('468', '认证模块', '-1', '192.168.1.147', '127.0.0.1', '8380', '认证', '2016-04-15 18:27:35', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('469', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8380', '认证', '2016-04-15 20:02:23', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('470', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-20 14:16:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('471', '认证模块', '-1', '192.168.1.135', '127.0.0.1', '8280', '认证', '2016-04-20 14:38:00', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('472', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-21 20:03:45', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('473', '认证模块', '-1', '192.168.1.173', '127.0.0.1', '8280', '认证', '2016-04-22 11:01:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('474', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-22 11:06:51', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('475', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-04-22 12:52:17', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('476', '认证模块', '-1', '192.168.1.135', '127.0.0.1', '8280', '认证', '2016-04-25 10:52:21', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('477', '认证模块', '-1', '192.168.1.135', '127.0.0.1', '8280', '认证', '2016-04-25 14:53:10', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('478', '认证模块', '-1', '192.168.1.135', '127.0.0.1', '8280', '认证', '2016-04-25 18:25:12', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('479', '认证模块', '-1', '192.168.1.135', '127.0.0.1', '8280', '认证', '2016-04-25 19:09:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('480', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-25 19:15:57', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('481', '认证模块', '-1', '192.168.1.168', '127.0.0.1', '8280', '认证', '2016-04-26 15:00:42', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('482', '认证模块', '-1', '192.168.1.147', '192.168.1.211', '8280', '认证', '2016-04-27 13:25:15', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('483', '认证模块', '-1', '192.168.1.100', '192.168.1.211', '8280', '认证', '2016-04-27 20:50:14', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('484', '认证模块', '-1', '192.168.1.168', '192.168.1.211', '8280', '认证', '2016-04-28 14:09:24', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('485', '认证模块', '-1', '192.168.1.177', '192.168.1.211', '8280', '认证', '2016-04-29 15:37:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('486', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-03 20:24:15', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('487', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-04 14:31:42', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('488', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-04 14:36:08', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('489', '认证模块', '-1', '192.168.1.168', '192.168.1.211', '8280', '认证', '2016-05-04 14:57:41', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('490', '认证模块', '-1', '192.168.1.168', '192.168.1.211', '8280', '认证', '2016-05-04 15:39:21', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('491', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-05 14:05:51', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('492', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-05 17:10:36', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('493', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-06 16:21:56', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('494', '认证模块', '-1', '192.168.1.168', '192.168.1.211', '8280', '认证', '2016-05-09 10:11:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('495', '认证模块', '-1', '192.168.1.127', '192.168.1.211', '8280', '认证', '2016-05-09 14:09:56', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('496', '认证模块', '-1', '192.168.1.177', '192.168.1.211', '8280', '认证', '2016-05-09 14:10:58', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('497', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-09 15:16:31', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('498', '认证模块', '-1', '192.168.1.179', '192.168.1.211', '8280', '认证', '2016-05-09 17:30:14', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('499', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-11 09:52:31', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('500', '认证模块', '-1', '192.168.1.135', '192.168.1.211', '8280', '认证', '2016-05-11 10:25:54', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('501', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-11 10:33:24', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('502', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-11 10:38:32', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('503', '认证模块', '-1', '192.168.1.173', '192.168.1.211', '8280', '认证', '2016-05-11 10:39:17', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('504', '认证模块', '-1', '192.168.1.135', '192.168.1.211', '8280', '认证', '2016-05-11 10:49:02', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('505', '认证模块', '-1', '192.168.1.177', '192.168.1.211', '8280', '认证', '2016-05-11 13:33:08', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('506', '认证模块', '-1', '192.168.1.177', '192.168.1.211', '8280', '认证', '2016-05-11 14:03:20', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('507', '认证模块', '-1', '192.168.1.168', '192.168.1.211', '8580', '认证', '2016-05-12 16:41:46', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('508', '认证模块', '0', '127.0.0.1', '192.168.4.59', '8080', '认证', '2016-05-13 11:06:38', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('509', '认证模块', '0', '127.0.0.1', '192.168.4.59', '8080', '认证', '2016-05-13 11:06:38', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('510', '认证模块', '0', '127.0.0.1', '192.168.4.59', '8080', '认证', '2016-05-13 11:06:38', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('511', '认证模块', '-1', '127.0.0.1', '172.17.10.27', '8080', '认证', '2016-05-13 11:22:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('512', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 15:19:03', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('513', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 15:34:39', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('514', '认证模块', '0', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 15:38:07', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('515', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 15:38:37', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('516', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 16:34:01', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('517', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 16:35:05', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('518', '认证模块', '-1', '127.0.0.1', '192.168.1.169', '8080', '认证', '2016-05-13 19:47:28', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('519', '认证模块', '-1', '127.0.0.1', '172.17.10.27', '8080', '认证', '2016-05-16 10:20:44', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('520', '认证模块', '-1', '127.0.0.1', '172.17.10.27', '8080', '认证', '2016-05-16 10:21:09', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('521', '认证模块', '-1', '127.0.0.1', '172.17.10.27', '8080', '认证', '2016-05-16 10:21:39', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('522', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-16 15:29:28', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('523', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-16 15:31:04', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('524', '认证模块', '0', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 10:32:35', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('525', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 10:34:52', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('526', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 10:57:52', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('527', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 11:01:30', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('528', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 14:03:28', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('529', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 15:30:08', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('530', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 15:34:00', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('531', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 15:38:07', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('532', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:04:41', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('533', '认证模块', '0', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:08:16', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('534', '认证模块', '0', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:08:27', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('535', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:08:59', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('536', '认证模块', '0', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:18:37', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('537', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:18:46', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('538', '认证模块', '0', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:19:15', '3', '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('539', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:19:19', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('540', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:19:49', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('541', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 16:19:49', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('542', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-19 17:48:46', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('543', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-20 17:03:23', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('544', '认证模块', '-1', '127.0.0.1', '192.168.4.49', '8080', '认证', '2016-05-20 17:04:29', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('545', '认证模块', '-1', '127.0.0.1', '172.17.10.14', '8080', '认证', '2016-05-26 16:16:27', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('546', '认证模块', '-1', '127.0.0.1', '192.168.4.32', '8080', '认证', '2016-05-26 16:55:23', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('547', '认证模块', '-1', '127.0.0.1', '192.168.4.32', '8080', '认证', '2016-05-26 16:59:56', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('548', '认证模块', '-1', '127.0.0.1', '192.168.4.32', '8080', '认证', '2016-05-26 17:01:57', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('549', '认证模块', '-1', '127.0.0.1', '172.17.10.14', '8080', '认证', '2016-05-27 11:02:57', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('550', '认证模块', '-1', '192.168.1.177', '192.168.126.1', '8280', '认证', '2016-06-02 11:27:20', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('551', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-02 14:04:25', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('552', '认证模块', '-1', '192.168.1.138', '192.168.126.1', '8280', '认证', '2016-06-02 14:21:07', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('553', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-03 18:28:29', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('554', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-03 19:09:38', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('555', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-06 09:58:32', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('556', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-06 10:25:30', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('557', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8280', '认证', '2016-06-06 10:37:14', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('558', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 14:42:20', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('559', '认证模块', '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 15:23:36', '2', '1', '账号[admin]登录成功', '0', null);
INSERT INTO `t_system_auditlog` VALUES ('560', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 16:17:57', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('561', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 16:43:34', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('562', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 16:44:49', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('563', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 16:46:37', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('564', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 16:47:35', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('565', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 17:01:54', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('566', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 17:06:20', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('567', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 17:26:58', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('568', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 18:16:08', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('569', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-06 18:20:33', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('570', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 14:43:39', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('571', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 16:33:54', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('572', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 16:42:31', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('573', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 16:43:12', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('574', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 16:47:02', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('575', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-07 20:12:55', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('576', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-08 10:14:34', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('577', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-12 09:46:11', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('578', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-12 16:45:27', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('579', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-12 17:17:50', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('580', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 13:35:14', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('581', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 14:59:05', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('582', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 16:21:12', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('583', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 16:30:48', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('584', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 17:01:21', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('585', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-13 17:49:50', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('586', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 10:04:24', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('587', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 10:37:43', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('588', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 13:57:03', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('589', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 14:15:41', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('590', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 14:28:07', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('591', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 14:42:22', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('592', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 15:53:32', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('593', null, '0', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 15:57:57', null, '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', null, null);
INSERT INTO `t_system_auditlog` VALUES ('594', null, '0', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 15:58:00', null, '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', null, null);
INSERT INTO `t_system_auditlog` VALUES ('595', null, '0', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 15:58:06', null, '2', '账号[admin]登录失败,失败原因:{用户登录验证异常,请联系管理员！}', null, null);
INSERT INTO `t_system_auditlog` VALUES ('596', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 16:33:52', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('597', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 17:41:00', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('598', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-14 18:01:47', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('599', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 09:46:24', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('600', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 10:59:07', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('601', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 16:19:19', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('602', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 16:32:56', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('603', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 16:42:00', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('604', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 16:44:08', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('605', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 16:52:12', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('606', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 17:01:46', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('607', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 17:32:03', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('608', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 17:41:50', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('609', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 18:08:07', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('610', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-15 18:11:47', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('611', null, '-1', '192.168.1.177', '192.168.126.1', '8080', '认证', '2016-06-17 10:54:15', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('612', null, '-1', '192.168.1.177', '192.168.126.1', '8080', '认证', '2016-06-17 10:59:52', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('613', null, '-1', '192.168.1.177', '192.168.126.1', '8080', '认证', '2016-06-17 11:00:11', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('614', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-20 10:24:24', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('615', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-21 16:47:55', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('616', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-22 16:30:32', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('617', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-27 14:21:30', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('618', null, '-1', '127.0.0.1', '192.168.126.1', '8080', '认证', '2016-06-27 14:27:28', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('619', null, '-1', '192.168.1.177', '192.168.126.1', '8080', '认证', '2016-06-29 14:38:35', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('620', null, '-1', '127.0.0.1', '172.16.0.109', '8080', '认证', '2016-07-06 10:41:53', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('621', null, '-1', '127.0.0.1', '192.168.70.8', '8080', '认证', '2016-07-06 11:18:41', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('622', null, '-1', '127.0.0.1', '192.168.70.8', '8080', '认证', '2016-07-06 11:18:41', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('623', null, '-1', '127.0.0.1', '192.168.70.8', '8080', '认证', '2016-07-06 11:22:11', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('624', null, '-1', '127.0.0.1', '192.168.70.8', '8080', '认证', '2016-07-06 11:25:02', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('625', null, '-1', '127.0.0.1', '192.168.70.8', '8080', '认证', '2016-07-06 11:25:49', null, '1', '账号[admin]登录成功', null, null);
INSERT INTO `t_system_auditlog` VALUES ('626', null, '-1', '127.0.0.1', '192.168.70.10', '8080', '认证', '2016-07-07 16:03:29', null, '1', '账号[admin]登录成功', null, null);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_system_user
-- ----------------------------
INSERT INTO `t_system_user` VALUES ('-1', 'admin', '超级管理员', 'efd1c61d4cdc7c7f', 'sdfsd@163.com', '13051126671', '0', '1', 'admin', '2016-03-08 17:46:34', 'admin', '2016-03-08 17:46:34', '2');

-- ----------------------------
-- Table structure for t_user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `t_user_feedback`;
CREATE TABLE `t_user_feedback` (
  `phone_num` varchar(255) DEFAULT NULL,
  `feedback_info` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_feedback
-- ----------------------------
INSERT INTO `t_user_feedback` VALUES ('11', '管理新全心全意');
INSERT INTO `t_user_feedback` VALUES ('11', 'CNN\'s I did dB');
INSERT INTO `t_user_feedback` VALUES ('', 'Chubb cm HSBC JDM');
INSERT INTO `t_user_feedback` VALUES ('11', 'Henrik\'s');
