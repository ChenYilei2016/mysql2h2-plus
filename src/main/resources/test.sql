CREATE TABLE `sync_metadata_job`
(
    `id`               bigint(20)    NOT NULL,
    `gmt_create`       datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator`          varchar(20)            DEFAULT NULL,
    `profile_id`       varchar(32)   NOT NULL COMMENT '亚马逊profileId',
    `available_status` tinyint(2)    NOT NULL DEFAULT '0' COMMENT '可以运行的任务：1 是，2 false',
    `job_type`         int(1)        NOT NULL DEFAULT '1' COMMENT '任务类型，1: 同步一个profileId下的各种信息',
    `job_status`       int(11)       NOT NULL DEFAULT '2' COMMENT '任务状态：1 未开始 2 排队中  3 进行中 4 暂停 5 完成  6 等待终止 7 终止 8 失败',
    `ip`               varchar(32)   NOT NULL DEFAULT '' COMMENT 'ip',
    `last_finish_time` bigint(20)             DEFAULT NULL COMMENT '上次完成时间',
    `run_num`          int(11)       NOT NULL DEFAULT '0' COMMENT '运行次数',
    `version`          int(11)       NOT NULL DEFAULT '0' COMMENT '版本号',
    `progress`         varchar(32)            DEFAULT NULL,
    `error_reason`     json NOT NULL,
    `remark`           varchar(256)  NOT NULL DEFAULT '',
    `env`              varchar(32)   NOT NULL DEFAULT 'local' COMMENT '区分环境拉取',
    `endpoint_url`     varchar(64)   NOT NULL DEFAULT '' COMMENT '调用亚马逊API endpoint',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profileId_jobtype` (`profile_id`, `job_type`),
    KEY `idx_available_status_job_status_job_type` (`available_status`, `job_status`, `job_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='同步信息任务表';