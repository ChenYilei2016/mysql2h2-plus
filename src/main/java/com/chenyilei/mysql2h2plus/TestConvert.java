package com.chenyilei.mysql2h2plus;

import com.chenyilei.mysql2h2plus.dlg.MysqlToH2Utils;
import com.chenyilei.mysql2h2plus.utils.FileUtils;
import com.intellij.patterns.StringPatternUtil;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
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


        File file = org.apache.commons.io.FileUtils.getFile("/Users/yileichen/Desktop/ownCode/mysql2h2-plus/src/main/resources/test.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Utils.convert(s);


        System.err.println(convert);
    }


    @Test
    public void testBtree() {


        File file = org.apache.commons.io.FileUtils.getFile("/Users/yileichen/Desktop/ownCode/mysql2h2-plus/src/main/resources/test_usingBtreeSql.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Utils.convert(s);


        System.err.println(convert);
    }

    @Test
    public void testC2() {
        //不区分大小写
        //追加序列号
        Pattern pattern = Pattern.compile("create\\s+\\w+\\s+(\\w+)\\s+on\\s+\\w+", Pattern.CASE_INSENSITIVE);


        File file = org.apache.commons.io.FileUtils.getFile("/Users/yileichen/Desktop/ownCode/mysql2h2-plus/src/main/resources/test.sql");
        String s = FileUtils.copyToString(file, Charset.defaultCharset());
        String convert = MysqlToH2Utils.convert(s);
        Matcher matcher = pattern.matcher(convert);



        System.err.println(matcher.groupCount());
        for (int i = 0; i < matcher.groupCount(); i++) {
            String group = matcher.group(i);
            System.err.println(group);
        }
    }
}
