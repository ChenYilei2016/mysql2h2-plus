CREATE TABLE `fenghuo_effect_ad_group` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                           `platform_name` varchar(100) DEFAULT NULL COMMENT '平台名称',
                                           `platform_id` varchar(100) DEFAULT NULL COMMENT '平台id',
                                           `market_place_name` varchar(100) DEFAULT NULL COMMENT '站点',
                                           `market_place_code` varchar(100) DEFAULT NULL COMMENT '站点编码',
                                           `first_center_name` varchar(100) DEFAULT NULL COMMENT '销售一级事业部名称',
                                           `first_center_code` varchar(100) DEFAULT NULL COMMENT '销售一级事业部编码',
                                           `second_center_name` varchar(100) DEFAULT NULL COMMENT '销售二级事业部名称',
                                           `second_center_code` varchar(100) DEFAULT NULL COMMENT '销售二级事业部编码',
                                           `store_manager` varchar(100) DEFAULT NULL COMMENT '店铺负责人',
                                           `store_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',
                                           `store_id` varchar(100) DEFAULT NULL COMMENT '店铺id',
                                           `bc_id` varchar(100) DEFAULT NULL COMMENT 'bc_id',
                                           `timezone` varchar(100) DEFAULT NULL COMMENT '时区',
                                           `advertising_type` varchar(100) DEFAULT NULL COMMENT '广告类型',
                                           `ad_mode` varchar(100) DEFAULT NULL COMMENT '广告模式',
                                           `campaign_bidding_strategy` varchar(100) DEFAULT NULL COMMENT '广告活动竞价策略',
                                           `portfolio_id` varchar(100) DEFAULT NULL COMMENT '广告组合id',
                                           `portfolio_name` varchar(100) DEFAULT NULL COMMENT '广告组合名称',
                                           `campaign_name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '广告活动名称',
                                           `campaign_id` varchar(100) DEFAULT NULL COMMENT '广告活动id',
                                           `campaign_status` varchar(100) DEFAULT NULL COMMENT '广告活动最新状态',
                                           `currency_code` varchar(100) DEFAULT NULL COMMENT '币种',
                                           `campaign_create_date` datetime DEFAULT NULL COMMENT '广告活动创建时间',
                                           `campaign_end_date` datetime DEFAULT NULL COMMENT '广告活动结束时间',
                                           `ad_group_name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '广告组名称',
                                           `ad_group_id` varchar(100) DEFAULT NULL COMMENT '广告组id',
                                           `ad_group_create_date` varchar(100) DEFAULT NULL COMMENT '广告组创建时间',
                                           `ad_group_end_date` varchar(100) DEFAULT NULL COMMENT '广告组结束时间',
                                           `ad_group_status` varchar(100) DEFAULT NULL COMMENT '广告组状态',
                                           `asin_cnt` bigint(20) DEFAULT NULL COMMENT 'asin数量',
                                           `sku_cnt` bigint(20) DEFAULT NULL COMMENT 'sku数量',
                                           `goods_cnt` bigint(20) DEFAULT NULL COMMENT 'goods数量',
                                           `impressions` bigint(20) DEFAULT NULL COMMENT '曝光量',
                                           `clicks` bigint(20) DEFAULT NULL COMMENT '点击量',
                                           `order_quantiy` bigint(20) DEFAULT NULL COMMENT '广告销售量',
                                           `sale_amt` decimal(18,6) DEFAULT NULL COMMENT '广告销售额',
                                           `cpc` decimal(18,6) DEFAULT NULL COMMENT '单个点击成本',
                                           `acos` decimal(18,6) DEFAULT NULL COMMENT '成本收入占比',
                                           `roas` decimal(18,6) DEFAULT NULL COMMENT '投入产出比',
                                           `ctr` decimal(18,6) DEFAULT NULL COMMENT '点击率',
                                           `cvr` decimal(18,6) DEFAULT NULL COMMENT '转化率',
                                           `target_cnt` bigint(20) DEFAULT NULL COMMENT '定位总数',
                                           `data_dt` varchar(100) DEFAULT NULL,
                                           `etl_data_dt` varchar(100) DEFAULT NULL,
                                           `update_data_dt` varchar(100) DEFAULT NULL,
                                           `cost` decimal(18,6) DEFAULT NULL COMMENT '广告花费',
                                           `user_id` varchar(100) DEFAULT NULL COMMENT '租户id',
                                           `user_name` varchar(100) DEFAULT NULL COMMENT '租户名称',
                                           `place_is` decimal(18,6) DEFAULT NULL,
                                           `vcpm` decimal(18,6) DEFAULT NULL,
                                           `campaign_budget_basic_amt` decimal(18,6) DEFAULT NULL,
                                           `campaign_budget_amt` decimal(18,6) DEFAULT NULL,
                                           `campaign_budget_status` decimal(18,6) DEFAULT NULL,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `idx_ad_group_unique` (`ad_group_id`,`market_place_code`,`advertising_type`,`store_id`,`campaign_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1932866 DEFAULT CHARSET=utf8 COMMENT='广告组效果统计'

