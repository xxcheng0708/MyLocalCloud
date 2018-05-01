

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_password` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_problem` varchar(80) CHARACTER SET utf8 NOT NULL,
  `u_answer` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `fileblock`;
CREATE TABLE `fileblock` (
  `f_id` int(11) NOT NULL,
  `b_name` varchar(80) NOT NULL,
  `b_covername` varchar(80) NOT NULL,
  `netdisk_id` int(11) NOT NULL,
  `f_key` varchar(80) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`f_id`),
  CONSTRAINT `file_fileblock_id` FOREIGN KEY (`f_id`) REFERENCES `file` (`f_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `netdisk`;
CREATE TABLE `netdisk` (
  `u_id` int(11) DEFAULT NULL,
  `n_name` varchar(20) CHARACTER SET utf8 NOT NULL,
  `n_comsumer_key` varchar(80) CHARACTER SET utf8 NOT NULL,
  `n_comsumer_secret` varchar(80) CHARACTER SET utf8 NOT NULL,
  `n_oauth_token` varchar(80) CHARACTER SET utf8 NOT NULL,
  `n_oauth_token_secret` varchar(80) CHARACTER SET utf8 NOT NULL,
  `n_supplier` varchar(80) CHARACTER SET utf8 NOT NULL,
  `n_used` int(11) NOT NULL,
  `n_capacity` int(11) NOT NULL,
  `n_description` varchar(80) CHARACTER SET utf8 NOT NULL,
  KEY `user_netdisk_id` (`u_id`),
  CONSTRAINT `user_netdisk_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `temp_download_file`;
CREATE TABLE `temp_download_file` (
  `tf_id` int(11) NOT NULL,
  `u_id` int(11) NOT NULL,
  `tf_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_date` datetime NOT NULL,
  `tf_tasktype` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_type` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_size` bigint(20) NOT NULL,
  `parent_id` int(11) NOT NULL,
  `tf_state` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_percent` int(11) NOT NULL,
  PRIMARY KEY (`tf_id`),
  KEY `user_tmp_download_id` (`u_id`),
  CONSTRAINT `user_tmp_download_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `temp_upload_file`;
CREATE TABLE `temp_upload_file` (
  `tf_id` int(11) NOT NULL,
  `u_id` int(11) NOT NULL,
  `tf_name` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_date` datetime NOT NULL,
  `tf_tasktype` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_type` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_size` bigint(20) NOT NULL,
  `parent_id` int(11) NOT NULL,
  `tf_state` varchar(80) CHARACTER SET utf8 NOT NULL,
  `tf_percent` int(11) NOT NULL,
  PRIMARY KEY (`tf_id`),
  KEY `user_tmp_id` (`u_id`),
  CONSTRAINT `user_tmp_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`u_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

