-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                            `username` varchar(50) NOT NULL COMMENT '用户名',
                            `password` varchar(50) NOT NULL COMMENT '密码',
                            `name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
                            `sex` tinyint(3) unsigned DEFAULT NULL COMMENT '性别;0-男 1-女',
                            `birth` datetime DEFAULT NULL COMMENT '出身日期',
                            `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                            `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
                            `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态;0-禁用 1-正常',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `pk_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';