package com.chenyilei.mysql2h2plus.test;

import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.chenyilei.mysql2h2plus.visit.MysqlToH2Helper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenyilei
 * @date 2022/09/14 11:17
 */

public class TestConvert {

    @Test
    public void testC() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS sync_metadata_job ;\n" +
                "CREATE TABLE IF NOT EXISTS sync_metadata_job (\n" +
                "\tid bigint NOT NULL PRIMARY KEY,\n" +
                "\tgmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "\tgmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "\tcreator varchar(20) NULL,\n" +
                "\tprofile_id varchar(32) NOT NULL COMMENT '亚马逊profileId',\n" +
                "\tavailable_status tinyint(2) NOT NULL DEFAULT 0 COMMENT '可以运行的任务：1 是，2 false',\n" +
                "\tjob_type int(1) NOT NULL DEFAULT 1 COMMENT '任务类型，1: 同步一个profileId下的各种信息',\n" +
                "\tjob_status int NOT NULL DEFAULT 2 COMMENT '任务状态：1 未开始 2 排队中  3 进行中 4 暂停 5 完成  6 等待终止 7 终止 8 失败',\n" +
                "\tip varchar(32) NOT NULL DEFAULT '' COMMENT 'ip',\n" +
                "\tlast_finish_time bigint NULL COMMENT '上次完成时间',\n" +
                "\trun_num int NOT NULL DEFAULT 0 COMMENT '运行次数',\n" +
                "\tversion int NOT NULL DEFAULT 0 COMMENT '版本号',\n" +
                "\tprogress varchar(32) NULL,\n" +
                "\terror_reason varchar(2048) NOT NULL DEFAULT '',\n" +
                "\tremark varchar(256) NOT NULL DEFAULT '',\n" +
                "\tenv varchar(32) NOT NULL DEFAULT 'local' COMMENT '区分环境拉取',\n" +
                "\tendpoint_url varchar(64) NOT NULL DEFAULT '' COMMENT '调用亚马逊API endpoint',\n" +
                "\tCONSTRAINT uk_profileId_jobtype_1 UNIQUE uk_profileId_jobtype_1 (profile_id, job_type),\n" +
                "\tUNIQUE idx_available_status_job_status_job_type_2 (available_status, job_status, job_type),\n" +
                "\tINDEX idx_available_status_job_status_3(available_status, job_status)\n" +
                ") COMMENT '同步信息任务表';;\n", convert);
    }


    @Test
    public void testBtree() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_usingBtreeSql.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS fenghuo_effect_ad_group ;\n" +
                "CREATE TABLE IF NOT EXISTS `fenghuo_effect_ad_group` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`platform_name` varchar(100) DEFAULT NULL COMMENT '平台名称',\n" +
                "\t`platform_id` varchar(100) DEFAULT NULL COMMENT '平台id',\n" +
                "\t`market_place_name` varchar(100) DEFAULT NULL COMMENT '站点',\n" +
                "\t`market_place_code` varchar(100) DEFAULT NULL COMMENT '站点编码',\n" +
                "\t`first_center_name` varchar(100) DEFAULT NULL COMMENT '销售一级事业部名称',\n" +
                "\t`first_center_code` varchar(100) DEFAULT NULL COMMENT '销售一级事业部编码',\n" +
                "\t`second_center_name` varchar(100) DEFAULT NULL COMMENT '销售二级事业部名称',\n" +
                "\t`second_center_code` varchar(100) DEFAULT NULL COMMENT '销售二级事业部编码',\n" +
                "\t`store_manager` varchar(100) DEFAULT NULL COMMENT '店铺负责人',\n" +
                "\t`store_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',\n" +
                "\t`store_id` varchar(100) DEFAULT NULL COMMENT '店铺id',\n" +
                "\t`bc_id` varchar(100) DEFAULT NULL COMMENT 'bc_id',\n" +
                "\t`timezone` varchar(100) DEFAULT NULL COMMENT '时区',\n" +
                "\t`advertising_type` varchar(100) DEFAULT NULL COMMENT '广告类型',\n" +
                "\t`ad_mode` varchar(100) DEFAULT NULL COMMENT '广告模式',\n" +
                "\t`campaign_bidding_strategy` varchar(100) DEFAULT NULL COMMENT '广告活动竞价策略',\n" +
                "\t`portfolio_id` varchar(100) DEFAULT NULL COMMENT '广告组合id',\n" +
                "\t`portfolio_name` varchar(100) DEFAULT NULL COMMENT '广告组合名称',\n" +
                "\t`campaign_name` varchar(300) DEFAULT NULL COMMENT '广告活动名称',\n" +
                "\t`campaign_id` varchar(100) DEFAULT NULL COMMENT '广告活动id',\n" +
                "\t`campaign_status` varchar(100) DEFAULT NULL COMMENT '广告活动最新状态',\n" +
                "\t`currency_code` varchar(100) DEFAULT NULL COMMENT '币种',\n" +
                "\t`campaign_create_date` datetime DEFAULT NULL COMMENT '广告活动创建时间',\n" +
                "\t`campaign_end_date` datetime DEFAULT NULL COMMENT '广告活动结束时间',\n" +
                "\t`ad_group_name` varchar(300) DEFAULT NULL COMMENT '广告组名称',\n" +
                "\t`ad_group_id` varchar(100) DEFAULT NULL COMMENT '广告组id',\n" +
                "\t`ad_group_create_date` varchar(100) DEFAULT NULL COMMENT '广告组创建时间',\n" +
                "\t`ad_group_end_date` varchar(100) DEFAULT NULL COMMENT '广告组结束时间',\n" +
                "\t`ad_group_status` varchar(100) DEFAULT NULL COMMENT '广告组状态',\n" +
                "\t`asin_cnt` bigint(20) DEFAULT NULL COMMENT 'asin数量',\n" +
                "\t`sku_cnt` bigint(20) DEFAULT NULL COMMENT 'sku数量',\n" +
                "\t`goods_cnt` bigint(20) DEFAULT NULL COMMENT 'goods数量',\n" +
                "\t`impressions` bigint(20) DEFAULT NULL COMMENT '曝光量',\n" +
                "\t`clicks` bigint(20) DEFAULT NULL COMMENT '点击量',\n" +
                "\t`order_quantiy` bigint(20) DEFAULT NULL COMMENT '广告销售量',\n" +
                "\t`sale_amt` decimal(18, 6) DEFAULT NULL COMMENT '广告销售额',\n" +
                "\t`cpc` decimal(18, 6) DEFAULT NULL COMMENT '单个点击成本',\n" +
                "\t`acos` decimal(18, 6) DEFAULT NULL COMMENT '成本收入占比',\n" +
                "\t`roas` decimal(18, 6) DEFAULT NULL COMMENT '投入产出比',\n" +
                "\t`ctr` decimal(18, 6) DEFAULT NULL COMMENT '点击率',\n" +
                "\t`cvr` decimal(18, 6) DEFAULT NULL COMMENT '转化率',\n" +
                "\t`target_cnt` bigint(20) DEFAULT NULL COMMENT '定位总数',\n" +
                "\t`data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`etl_data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`update_data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`cost` decimal(18, 6) DEFAULT NULL COMMENT '广告花费',\n" +
                "\t`user_id` varchar(100) DEFAULT NULL COMMENT '租户id',\n" +
                "\t`user_name` varchar(100) DEFAULT NULL COMMENT '租户名称',\n" +
                "\t`place_is` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`vcpm` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_basic_amt` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_amt` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_status` decimal(18, 6) DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE idx_ad_group_unique_1 (`ad_group_id`, `market_place_code`, `advertising_type`, `store_id`, `campaign_id`)\n" +
                ") COMMENT '广告组效果统计';\n" +
                "DROP TABLE IF EXISTS test2 ;\n" +
                "CREATE TABLE IF NOT EXISTS `test2` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`platform_name` varchar(100) DEFAULT NULL COMMENT '平台名称',\n" +
                "\t`platform_id` varchar(100) DEFAULT NULL COMMENT '平台id',\n" +
                "\t`market_place_name` varchar(100) DEFAULT NULL COMMENT '站点',\n" +
                "\t`market_place_code` varchar(100) DEFAULT NULL COMMENT '站点编码',\n" +
                "\t`first_center_name` varchar(100) DEFAULT NULL COMMENT '销售一级事业部名称',\n" +
                "\t`first_center_code` varchar(100) DEFAULT NULL COMMENT '销售一级事业部编码',\n" +
                "\t`second_center_name` varchar(100) DEFAULT NULL COMMENT '销售二级事业部名称',\n" +
                "\t`second_center_code` varchar(100) DEFAULT NULL COMMENT '销售二级事业部编码',\n" +
                "\t`store_manager` varchar(100) DEFAULT NULL COMMENT '店铺负责人',\n" +
                "\t`store_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',\n" +
                "\t`store_id` varchar(100) DEFAULT NULL COMMENT '店铺id',\n" +
                "\t`bc_id` varchar(100) DEFAULT NULL COMMENT 'bc_id',\n" +
                "\t`timezone` varchar(100) DEFAULT NULL COMMENT '时区',\n" +
                "\t`advertising_type` varchar(100) DEFAULT NULL COMMENT '广告类型',\n" +
                "\t`ad_mode` varchar(100) DEFAULT NULL COMMENT '广告模式',\n" +
                "\t`campaign_bidding_strategy` varchar(100) DEFAULT NULL COMMENT '广告活动竞价策略',\n" +
                "\t`portfolio_id` varchar(100) DEFAULT NULL COMMENT '广告组合id',\n" +
                "\t`portfolio_name` varchar(100) DEFAULT NULL COMMENT '广告组合名称',\n" +
                "\t`campaign_name` varchar(300) DEFAULT NULL COMMENT '广告活动名称',\n" +
                "\t`campaign_id` varchar(100) DEFAULT NULL COMMENT '广告活动id',\n" +
                "\t`campaign_status` varchar(100) DEFAULT NULL COMMENT '广告活动最新状态',\n" +
                "\t`currency_code` varchar(100) DEFAULT NULL COMMENT '币种',\n" +
                "\t`campaign_create_date` datetime DEFAULT NULL COMMENT '广告活动创建时间',\n" +
                "\t`campaign_end_date` datetime DEFAULT NULL COMMENT '广告活动结束时间',\n" +
                "\t`ad_group_name` varchar(300) DEFAULT NULL COMMENT '广告组名称',\n" +
                "\t`ad_group_id` varchar(100) DEFAULT NULL COMMENT '广告组id',\n" +
                "\t`ad_group_create_date` varchar(100) DEFAULT NULL COMMENT '广告组创建时间',\n" +
                "\t`ad_group_end_date` varchar(100) DEFAULT NULL COMMENT '广告组结束时间',\n" +
                "\t`ad_group_status` varchar(100) DEFAULT NULL COMMENT '广告组状态',\n" +
                "\t`asin_cnt` bigint(20) DEFAULT NULL COMMENT 'asin数量',\n" +
                "\t`sku_cnt` bigint(20) DEFAULT NULL COMMENT 'sku数量',\n" +
                "\t`goods_cnt` bigint(20) DEFAULT NULL COMMENT 'goods数量',\n" +
                "\t`impressions` bigint(20) DEFAULT NULL COMMENT '曝光量',\n" +
                "\t`clicks` bigint(20) DEFAULT NULL COMMENT '点击量',\n" +
                "\t`order_quantiy` bigint(20) DEFAULT NULL COMMENT '广告销售量',\n" +
                "\t`sale_amt` decimal(18, 6) DEFAULT NULL COMMENT '广告销售额',\n" +
                "\t`cpc` decimal(18, 6) DEFAULT NULL COMMENT '单个点击成本',\n" +
                "\t`acos` decimal(18, 6) DEFAULT NULL COMMENT '成本收入占比',\n" +
                "\t`roas` decimal(18, 6) DEFAULT NULL COMMENT '投入产出比',\n" +
                "\t`ctr` decimal(18, 6) DEFAULT NULL COMMENT '点击率',\n" +
                "\t`cvr` decimal(18, 6) DEFAULT NULL COMMENT '转化率',\n" +
                "\t`target_cnt` bigint(20) DEFAULT NULL COMMENT '定位总数',\n" +
                "\t`data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`etl_data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`update_data_dt` varchar(100) DEFAULT NULL,\n" +
                "\t`cost` decimal(18, 6) DEFAULT NULL COMMENT '广告花费',\n" +
                "\t`user_id` varchar(100) DEFAULT NULL COMMENT '租户id',\n" +
                "\t`user_name` varchar(100) DEFAULT NULL COMMENT '租户名称',\n" +
                "\t`place_is` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`vcpm` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_basic_amt` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_amt` decimal(18, 6) DEFAULT NULL,\n" +
                "\t`campaign_budget_status` decimal(18, 6) DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE idx_ad_group_unique_2 (`ad_group_id`, `market_place_code`, `advertising_type`, `store_id`, `campaign_id`)\n" +
                ") COMMENT '广告组效果统计';\n", convert);
    }


    @Test
    public void testCreateIndex() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_createindex.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS sync_metadata_job ;\n" +
                "CREATE TABLE IF NOT EXISTS sync_metadata_job (\n" +
                "\tid bigint NOT NULL PRIMARY KEY,\n" +
                "\tgmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "\tgmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "\tcreator varchar(20) NULL,\n" +
                "\tprofile_id varchar(32) NOT NULL COMMENT '亚马逊profileId',\n" +
                "\tavailable_status tinyint(2) NOT NULL DEFAULT 0 COMMENT '可以运行的任务：1 是，2 false',\n" +
                "\tjob_type int(1) NOT NULL DEFAULT 1 COMMENT '任务类型，1: 同步一个profileId下的各种信息',\n" +
                "\tjob_status int NOT NULL DEFAULT 2 COMMENT '任务状态：1 未开始 2 排队中  3 进行中 4 暂停 5 完成  6 等待终止 7 终止 8 失败',\n" +
                "\tip varchar(32) NOT NULL DEFAULT '' COMMENT 'ip',\n" +
                "\tlast_finish_time bigint NULL COMMENT '上次完成时间',\n" +
                "\trun_num int NOT NULL DEFAULT 0 COMMENT '运行次数',\n" +
                "\tversion int NOT NULL DEFAULT 0 COMMENT '版本号',\n" +
                "\tprogress varchar(32) NULL,\n" +
                "\terror_reason text NOT NULL DEFAULT '',\n" +
                "\tremark text NOT NULL,\n" +
                "\tenv varchar(32) NOT NULL DEFAULT 'local' COMMENT '区分环境拉取',\n" +
                "\tendpoint_url varchar(64) NOT NULL DEFAULT '' COMMENT '调用亚马逊API endpoint',\n" +
                "\tCONSTRAINT uk_profileId_jobtype_1 UNIQUE uk_profileId_jobtype_1 (profile_id, job_type),\n" +
                "\tUNIQUE idx_available_status_job_status_job_type_2 (available_status, job_status, job_type),\n" +
                "\tINDEX idx_available_status_job_status_3(available_status, job_status)\n" +
                ") COMMENT '同步信息任务表';;\n", convert);
    }

    @Test
    public void testDropTable() {
        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_droptable.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);

        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS sync_metadata_job ;\n" +
                "CREATE TABLE IF NOT EXISTS `sync_metadata_job` (\n" +
                "\tid bigint NOT NULL PRIMARY KEY,\n" +
                "\tgmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "\tgmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "\tcreator varchar(20) NULL,\n" +
                "\tprofile_id varchar(32) NOT NULL COMMENT '亚马逊profileId',\n" +
                "\tavailable_status tinyint(2) NOT NULL DEFAULT 0 COMMENT '可以运行的任务：1 是，2 false',\n" +
                "\tjob_type int(1) NOT NULL DEFAULT 1 COMMENT '任务类型，1: 同步一个profileId下的各种信息',\n" +
                "\tjob_status int NOT NULL DEFAULT 2 COMMENT '任务状态：1 未开始 2 排队中  3 进行中 4 暂停 5 完成  6 等待终止 7 终止 8 失败',\n" +
                "\tip varchar(32) NOT NULL DEFAULT '' COMMENT 'ip',\n" +
                "\tlast_finish_time bigint NULL COMMENT '上次完成时间',\n" +
                "\trun_num int NOT NULL DEFAULT 0 COMMENT '运行次数',\n" +
                "\tversion int NOT NULL DEFAULT 0 COMMENT '版本号',\n" +
                "\tprogress varchar(32) NULL,\n" +
                "\terror_reason varchar(2048) NOT NULL DEFAULT '',\n" +
                "\tremark varchar(256) NOT NULL DEFAULT '',\n" +
                "\tenv varchar(32) NOT NULL DEFAULT 'local' COMMENT '区分环境拉取',\n" +
                "\tendpoint_url varchar(64) NOT NULL DEFAULT '' COMMENT '调用亚马逊API endpoint',\n" +
                "\tCONSTRAINT uk_profileId_jobtype_1 UNIQUE uk_profileId_jobtype_1 (profile_id, job_type),\n" +
                "\tUNIQUE idx_available_status_job_status_job_type_2 (available_status, job_status, job_type)\n" +
                ") COMMENT '同步信息任务表';;\n", convert);
    }

    @Test
    public void testVirtual() {
        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_virtual.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);
        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS sales ;\n" +
                "CREATE TABLE IF NOT EXISTS `sales` (\n" +
                "\t`id` int(11) NOT NULL,\n" +
                "\t`province` varchar(12) GENERATED ALWAYS AS (substr(`confirmation_number`, 9, 2)),\n" +
                "\t`customer_id` int(11) NOT NULL,\n" +
                "\t`confirmation_number` varchar(12) NOT NULL,\n" +
                "\t`order_id` int(11) DEFAULT NULL,\n" +
                "\t`order_type` varchar(10) DEFAULT NULL,\n" +
                "\t`start_date` date NOT NULL,\n" +
                "\t`end_date` date DEFAULT NULL\n" +
                ");\n", convert);
    }

    @Test
    public void testVirtual_format_timestamp() {
        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/testVirtual_format_timestamp.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        /////
        System.err.println("================================================================");
        String convert = MysqlToH2Helper.convert(s);
        System.err.println("================================================================");
        System.err.println(convert);
        Assert.assertEquals("SET NAMES utf8mb4;;\n" +
                "SET FOREIGN_KEY_CHECKS = 0;;\n" +
                "DROP TABLE IF EXISTS hello ;\n" +
                "-- CREATE ALIAS IF NOT EXISTS date_format FOR \"com.chenyilei.mysql2h2plus.test.h2Funcs.H2Function.date_format\";\n" +
                "CREATE TABLE IF NOT EXISTS `hello` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`task_last_heart_beat` bigint(20) DEFAULT '0' COMMENT '最近一次的心跳时间',\n" +
                "\t`last_end_time` bigint(20) DEFAULT NULL COMMENT '上次完成时间',\n" +
                "\t`visual_task_last_heart_beat` varchar(32) GENERATED ALWAYS AS (date_format(from_unixtime(`task_last_heart_beat` / 1000),'%Y-%m-%d %H:%i:%s')),\n" +
                "\t`visual_last_end_time` varchar(32) GENERATED ALWAYS AS (date_format(from_unixtime(`last_end_time` / 1000),'%Y-%m-%d %H:%i:%s')),\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ");;\n", convert);
    }

    @Test
    public void testPattern() {
        //不区分大小写
        //追加序列号
        Pattern pattern = Pattern.compile("create\\s+\\w+\\s+(\\w+)\\s+on\\s+\\w+", Pattern.CASE_INSENSITIVE);


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);
        Matcher matcher = pattern.matcher(convert);


        System.err.println(matcher.groupCount());
        for (int i = 0; i < matcher.groupCount(); i++) {
            String group = matcher.group(i);
            System.err.println(group);
        }
    }

    @Test
    public void testIndexLengthConvert() {
        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_indexLength.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);

        System.err.println(convert);
        Assert.assertEquals("DROP TABLE IF EXISTS oauth2_authorization ;\n" +
                "CREATE TABLE IF NOT EXISTS `oauth2_authorization` (\n" +
                "\t`id` VARCHAR(100) NOT NULL COMMENT '主键ID' COLLATE utf8mb4_general_ci,\n" +
                "\t`authorization_code_value` VARCHAR(1024) NULL DEFAULT NULL COMMENT '授权码code值对象' COLLATE utf8mb4_general_ci,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tINDEX idx_authorization_code_value_1(`authorization_code_value`)\n" +
                ") COMMENT '认证通过的客户端、令牌、用户信息' COLLATE utf8mb4_general_ci;;\n", convert);
    }

    @Test
    public void uri() throws Exception {
        final URI uri = new URI("https://github.com/ChenYilei2016/mysql2h2-plus");
    }
}
