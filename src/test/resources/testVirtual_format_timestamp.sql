SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- CREATE ALIAS IF NOT EXISTS date_format FOR "com.chenyilei.mysql2h2plus.test.h2Funcs.H2Function.date_format";

CREATE TABLE `hello` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_last_heart_beat` bigint(20) DEFAULT '0' COMMENT '最近一次的心跳时间',
  `last_end_time` bigint(20) DEFAULT NULL COMMENT '上次完成时间',
  `visual_task_last_heart_beat` varchar(32) GENERATED ALWAYS AS (date_format(from_unixtime((`task_last_heart_beat` / 1000)),_utf8mb4'%Y-%m-%d %H:%i:%s')) VIRTUAL,
  `visual_last_end_time` varchar(32) GENERATED ALWAYS AS (date_format(from_unixtime((`last_end_time` / 1000)),_utf8mb4'%Y-%m-%d %H:%i:%s')) VIRTUAL,
  PRIMARY KEY (`id`)
) ;

