package com.chenyilei.mysql2h2plus.dlg;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.util.JdbcConstants;
import com.chenyilei.mysql2h2plus.context.DlgMetaContext;
import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.chenyilei.mysql2h2plus.visit.ZbyMysqlToH2Visitor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MysqlToH2Utils {
    private static Pattern createIndexPattern = Pattern.compile("CREATE\\s+(UNIQUE\\s)?\\w+\\s+(\\w+)\\s+ON\\s+\\w+");

    private static InputStream readFile(String filepath) throws IOException {
        InputStream input = new BufferedInputStream(new FileInputStream(filepath));
        PushbackInputStream pb = new PushbackInputStream(input, 2);
        byte[] magicBytes = new byte[2];
        if (pb.read(magicBytes) != 2) {
            throw new RuntimeException("读取文件出错[" + filepath + "]");
        }
        pb.unread(magicBytes);
        ByteBuffer bb = ByteBuffer.wrap(magicBytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        short magic = bb.getShort();
        return magic == (short) GZIPInputStream.GZIP_MAGIC ? new GZIPInputStream(pb) : pb;
    }
    /**
     * CREATE TABLE
     * CREATE TABLE IF NOT EXISTS
     */

    /**
     * 进行转换处
     *
     * @param mysqlTxt
     * @return
     */
    public static String convert(String mysqlTxt) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(mysqlTxt, JdbcConstants.MYSQL);
        LinkedList<SQLStatement> orderedSqlStatements = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        ZbyMysqlToH2Visitor visitor = new ZbyMysqlToH2Visitor(sb);

        Map<String, MySqlCreateTableStatement> mySqlCreateTableStatementMap = new LinkedHashMap<>();
        List<SQLCreateIndexStatement> createIndexStatementList = new ArrayList<>();
        List<SQLStatement> elseStatementList = new ArrayList<>();

        for (SQLStatement statement : sqlStatements) {
            if (statement instanceof MySqlCreateTableStatement) {
                mySqlCreateTableStatementMap.put(((MySqlCreateTableStatement) statement).getName().getSimpleName(), ((MySqlCreateTableStatement) statement));
            } else if (statement instanceof SQLCreateIndexStatement) {
                createIndexStatementList.add((SQLCreateIndexStatement) statement);
            } else {
                elseStatementList.add(statement);
            }
        }

        if (DlgMetaContext.mergeOutCreateIndexIntoCreateTableSql) {
            //存在的创建表
            Iterator<SQLCreateIndexStatement> iterator = createIndexStatementList.iterator();
            if (iterator.hasNext()) {
                SQLCreateIndexStatement sqlCreateIndexStatement = iterator.next();
                String indexName = sqlCreateIndexStatement.getName().getSimpleName();
                String tableName = sqlCreateIndexStatement.getTableName();
                List<SQLSelectOrderByItem> items = sqlCreateIndexStatement.getItems();

                MySqlCreateTableStatement mySqlCreateTableStatement = mySqlCreateTableStatementMap.get(tableName);

                if (mySqlCreateTableStatement != null) {
                    iterator.remove();
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

        mySqlCreateTableStatementMap.forEach((s, mySqlCreateTableStatement) -> {
            orderedSqlStatements.addLast(mySqlCreateTableStatement);
        });
        createIndexStatementList.forEach(orderedSqlStatements::addLast);
        elseStatementList.forEach(orderedSqlStatements::addLast);

        for (SQLStatement statement : orderedSqlStatements) {
            //原先有表的话先删除
            if (DlgMetaContext.dropTableIfExists) {
                if (statement instanceof MySqlCreateTableStatement) {
                    String tableName = ((MySqlCreateTableStatement) statement).getTableSource().getName().getSimpleName();
                    sb.append("DROP TABLE IF EXISTS ").append(tableName).append(" ;").append("\n");
                }
            }
            statement.accept(visitor);
            //防止无分号
            sb.append(";");
            sb.append("\n");
        }
        String h2Sql = sb.toString();
//        h2Sql = createIndexUnique(h2Sql);
        return h2Sql;
    }

    private static String createIndexUnique(String h2Sql) {
        Matcher matcher = createIndexPattern.matcher(h2Sql);

        StringBuffer sb = new StringBuffer();
        //find 是向前推进的
        //group(0) 原句
        //group(1~..) 分组
        while (matcher.find()) {

            String createIndexLine = matcher.group(0);
            String indexName = matcher.group(2);
            String uniqueIndexLine = createIndexLine.replace(indexName, indexName + "_" + ZbyMysqlToH2Visitor.atomicInteger.getAndIncrement());

            matcher.appendReplacement(sb, uniqueIndexLine);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void convert(String source, String target) throws IOException {
        try (InputStream fileIn = readFile(source); FileOutputStream fileOut = new FileOutputStream(target)) {
            String mysqlText = FileUtils.copyToString(fileIn, StandardCharsets.UTF_8);
            fileOut.write(convert(mysqlText).getBytes(StandardCharsets.UTF_8));
        }
    }
}
