package com.chenyilei.mysql2h2plus.dlg;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.chenyilei.mysql2h2plus.visit.ZbyMysqlToH2Visitor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MysqlToH2Utils {
    private static Pattern createIndexPattern = Pattern.compile("create\\s+(UNIQUE\\s)?\\w+\\s+(\\w+)\\s+on\\s+\\w+", Pattern.CASE_INSENSITIVE);

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
     * 进行转换处
     *
     * @param mysqlTxt
     * @return
     */
    public static String convert(String mysqlTxt) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(mysqlTxt, JdbcConstants.MYSQL);
        StringBuilder sb = new StringBuilder();
        ZbyMysqlToH2Visitor visitor = new ZbyMysqlToH2Visitor(sb);
        for (SQLStatement statement : sqlStatements) {

            statement.accept(visitor);
            //防止无分号
            sb.append(";");
            sb.append("\n");
        }
        String h2Sql = sb.toString();

        h2Sql = createIndexUnique(h2Sql);

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
