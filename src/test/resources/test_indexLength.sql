CREATE TABLE `oauth2_authorization` (
   `id` VARCHAR(100) NOT NULL COMMENT '主键ID' COLLATE 'utf8mb4_general_ci',
   `authorization_code_value` VARCHAR(1024) NULL DEFAULT NULL COMMENT '授权码code值对象' COLLATE 'utf8mb4_general_ci',
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `idx_authorization_code_value` ( `authorization_code_value`(128) ) USING BTREE
)
COMMENT='认证通过的客户端、令牌、用户信息'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;