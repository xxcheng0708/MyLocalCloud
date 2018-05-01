/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50517
Source Host           : 127.0.0.1:3306
Source Database       : cloud

Target Server Type    : MYSQL
Target Server Version : 50517
File Encoding         : 65001

Date: 2018-05-01 20:05:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NOT NULL,
  `f_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `f_date` varchar(80) NOT NULL,
  `f_type` varchar(80) CHARACTER SET utf8 NOT NULL,
  `f_size` bigint(20) NOT NULL,
  `parent_id` int(11) NOT NULL,
  PRIMARY KEY (`f_id`),
  KEY `user_file_id` (`u_id`),
  CONSTRAINT `user_file_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for fileblock
-- ----------------------------
DROP TABLE IF EXISTS `fileblock`;
CREATE TABLE `fileblock` (
  `f_id` int(11) NOT NULL,
  `b_name` varchar(80) NOT NULL,
  `b_covername` varchar(80) CHARACTER SET utf8 NOT NULL,
  `f_key` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`f_id`),
  CONSTRAINT `file_fileblock_id` FOREIGN KEY (`f_id`) REFERENCES `file` (`f_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for hdfs
-- ----------------------------
DROP TABLE IF EXISTS `hdfs`;
CREATE TABLE `hdfs` (
  `hdfs_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) DEFAULT NULL,
  `hdfs_ip` varchar(20) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`hdfs_id`),
  KEY `user_hdfs_id` (`u_id`) USING BTREE,
  CONSTRAINT `user_hdfs_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for temp_download_file
-- ----------------------------
DROP TABLE IF EXISTS `temp_download_file`;
CREATE TABLE `temp_download_file` (
  `tf_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NOT NULL,
  `tf_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_date` varchar(80) NOT NULL,
  `tf_tasktype` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_type` varchar(80) CHARACTER SET utf8 NOT NULL,
  `parent_id` int(11) NOT NULL,
  `tf_state` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`tf_id`),
  KEY `user_tmp_download_id` (`u_id`),
  CONSTRAINT `user_tmp_download_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for temp_upload_file
-- ----------------------------
DROP TABLE IF EXISTS `temp_upload_file`;
CREATE TABLE `temp_upload_file` (
  `tf_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NOT NULL,
  `tf_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_date` varchar(80) NOT NULL,
  `tf_tasktype` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_type` varchar(80) CHARACTER SET utf8 NOT NULL,
  `parent_id` int(11) NOT NULL,
  `tf_state` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`tf_id`),
  KEY `user_tmp_id` (`u_id`),
  CONSTRAINT `user_tmp_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_password` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_problem` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_answer` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
