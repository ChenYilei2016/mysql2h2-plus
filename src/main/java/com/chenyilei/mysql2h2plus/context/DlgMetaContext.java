package com.chenyilei.mysql2h2plus.context;

/**
 * 目前全局static, 不区分project
 * @author chenyilei
 * @date 2022/09/26 11:09
 */
public class DlgMetaContext {

    /**
     * sql : createTableIfNotExists
     */
    public static boolean createTableIfNotExists = true;
    /**
     * sql dropTableIfExists
     */
    public static boolean dropTableIfExists = true;
    /**
     * 如果create index是在创建表外执行的, 是否融合到表内
     */
    public static boolean mergeOutCreateIndexIntoCreateTableSql = true;
}
