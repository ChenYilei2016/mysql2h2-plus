create table if not exists sync_metadata_job
(
    id               bigint                                  not null
        primary key,
    gmt_create       datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified     datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    creator          varchar(20)                             null,
    profile_id       varchar(32)                             not null comment '亚马逊profileId',
    available_status tinyint(2)    default 0                 not null comment '可以运行的任务：1 是，2 false',
    job_type         int(1)        default 1                 not null comment '任务类型，1: 同步一个profileId下的各种信息',
    job_status       int           default 2                 not null comment '任务状态：1 未开始 2 排队中  3 进行中 4 暂停 5 完成  6 等待终止 7 终止 8 失败',
    ip               varchar(32)   default ''                not null comment 'ip',
    last_finish_time bigint                                  null comment '上次完成时间',
    run_num          int           default 0                 not null comment '运行次数',
    version          int           default 0                 not null comment '版本号',
    progress         varchar(32)                             null,
    error_reason     varchar(2048) default ''                not null,
    remark           varchar(256)  default ''                not null,
    env              varchar(32)   default 'local'           not null comment '区分环境拉取',
    endpoint_url     varchar(64)   default ''                not null comment '调用亚马逊API endpoint',
    constraint uk_profileId_jobtype
        unique (profile_id, job_type)
)
    comment '同步信息任务表' charset = utf8;

create unique index idx_available_status_job_status_job_type
    on sync_metadata_job (available_status, job_status, job_type);

create  index idx_available_status_job_status
    on sync_metadata_job (available_status, job_status);
