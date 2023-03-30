package com.chenyilei.mysql2h2plus.visit;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.util.JdbcConstants;
import com.chenyilei.mysql2h2plus.context.DlgMetaContext;
import com.chenyilei.mysql2h2plus.utils.MyBaseUtils;
import com.chenyilei.mysql2h2plus.utils.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlToH2Helper {
    private static Pattern createIndexPattern = Pattern.compile("CREATE\\s+(UNIQUE\\s)?\\w+\\s+(\\w+)\\s+ON\\s+\\w+");
    /**
     * 进行转换处
     * CREATE TABLE
     * CREATE TABLE IF NOT EXISTS
     *
     * @param mysqlTxt
     * @return
     */
    public static String convert(String mysqlTxt) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(mysqlTxt, JdbcConstants.MYSQL);

        StringBuilder sb = new StringBuilder();
        MysqlToH2Visitor visitor = new MysqlToH2Visitor(sb);

        Map<String, MySqlCreateTableStatement> mySqlCreateTableStatementMap = new LinkedHashMap<>();
        List<SQLCreateIndexStatement> createIndexStatementList = new ArrayList<>();
        List<SQLStatement> removeStatementList = new LinkedList<>();

        for (SQLStatement statement : sqlStatements) {
            if (statement instanceof MySqlCreateTableStatement) {
                mySqlCreateTableStatementMap.put(unquote(((MySqlCreateTableStatement) statement).getName()), ((MySqlCreateTableStatement) statement));
            } else if (statement instanceof SQLCreateIndexStatement) {
                createIndexStatementList.add((SQLCreateIndexStatement) statement);
            } else if (statement instanceof SQLDropTableStatement && DlgMetaContext.dropTableIfExists) {
                //去除原先的drop sql
                removeStatementList.add(statement);
            } else {
            }
        }

        mergeOutCreateIndexIntoCreateTableSql(mySqlCreateTableStatementMap, createIndexStatementList, removeStatementList);

        sqlStatements.removeAll(removeStatementList);

        for (SQLStatement statement : sqlStatements) {
            //原先有表的话先删除
            if (DlgMetaContext.dropTableIfExists) {
                if (statement instanceof MySqlCreateTableStatement) {
                    String tableName = unquote(((MySqlCreateTableStatement) statement).getTableSource().getName());
                    sb.append("DROP TABLE IF EXISTS ").append(tableName).append(" ;").append("\n");
                }
            }
            statement.accept(visitor);
            //防止无分号
            sb.append(";");
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void mergeOutCreateIndexIntoCreateTableSql(Map<String, MySqlCreateTableStatement> mySqlCreateTableStatementMap, List<SQLCreateIndexStatement> createIndexStatementList, List<SQLStatement> removeStatementList) {
        if (DlgMetaContext.mergeOutCreateIndexIntoCreateTableSql) {
            //存在的创建表
            for (SQLCreateIndexStatement sqlCreateIndexStatement : createIndexStatementList) {
                String indexName = unquote(sqlCreateIndexStatement.getName());
                String tableName = sqlCreateIndexStatement.getTableName();
                List<SQLSelectOrderByItem> items = sqlCreateIndexStatement.getItems();

                MySqlCreateTableStatement mySqlCreateTableStatement = mySqlCreateTableStatementMap.get(unquote(tableName));

                if (mySqlCreateTableStatement != null) {
                    removeStatementList.add(sqlCreateIndexStatement);

                    SQLTableElement sqlKey = null;
                    if ("UNIQUE".equals(sqlCreateIndexStatement.getType())) {
                        MySqlUnique mySqlUnique = new MySqlUnique();
                        mySqlUnique.setParent(sqlCreateIndexStatement.getParent());
                        mySqlUnique.setName(new SQLIdentifierExpr(indexName));
                        items.forEach(mySqlUnique::addColumn);
                        sqlKey = mySqlUnique;
                    } else {
                        MySqlTableIndex mySqlTableIndex = new MySqlTableIndex();
                        mySqlTableIndex.setParent(sqlCreateIndexStatement.getParent());
                        mySqlTableIndex.setName(new SQLIdentifierExpr(indexName));
                        items.forEach(mySqlTableIndex::addColumn);
                        sqlKey = mySqlTableIndex;
                    }
                    mySqlCreateTableStatement.getTableElementList().add(sqlKey);
                }
            }
        }
    }

    /**
     * 正则方式替换唯一索引 , 没啥用了
     */
    private static String createIndexUnique(String h2Sql) {
        Matcher matcher = createIndexPattern.matcher(h2Sql);

        StringBuffer sb = new StringBuffer();
        //find 是向前推进的
        //group(0) 原句
        //group(1~..) 分组
        while (matcher.find()) {

            String createIndexLine = matcher.group(0);
            String indexName = matcher.group(2);
            String uniqueIndexLine = createIndexLine.replace(indexName, indexName + "_" + MysqlToH2Visitor.atomicInteger.getAndIncrement());

            matcher.appendReplacement(sb, uniqueIndexLine);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void convert(String source, String target) throws IOException {
        try (InputStream fileIn = MyBaseUtils.readFile(source); FileOutputStream fileOut = new FileOutputStream(target)) {
            String mysqlText = FileUtils.copyToString(fileIn, StandardCharsets.UTF_8);
            fileOut.write(convert(mysqlText).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String unquote(String name) {
        if (name.startsWith("`") && name.endsWith("`")) {
            name = name.substring(1, name.length() - 1);
        }

        return name;
    }

    public static String unquote(SQLName sqlName) {
        String name = sqlName.getSimpleName();
        if (name.startsWith("`") && name.endsWith("`")) {
            name = name.substring(1, name.length() - 1);
        }
        return name;
    }
}
