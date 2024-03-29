package com.chenyilei.mysql2h2plus.test;

import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.chenyilei.mysql2h2plus.visit.MysqlToH2Helper;
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
    }


    @Test
    public void testBtree() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_usingBtreeSql.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
    }


    @Test
    public void testCreateIndex() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_createindex.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
    }

    @Test
    public void testDropTable() {


        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_droptable.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);


        System.err.println(convert);
    }

    @Test
    public void testVirtual() {
        File file = org.apache.commons.io.FileUtils.getFile("src/test/resources/test_virtual.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Helper.convert(s);
        System.err.println(convert);
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
    public void uri() throws Exception{
        final URI uri = new URI("https://github.com/ChenYilei2016/mysql2h2-plus");
    }
}
