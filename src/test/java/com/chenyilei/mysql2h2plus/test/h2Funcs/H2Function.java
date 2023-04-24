package com.chenyilei.mysql2h2plus.test.h2Funcs;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class H2Function {

    /**
     * 时间格式化函数
     *
     * @param date    日期
     * @param pattern 格式化pattern
     * @return 格式化后的字符串
     */
    public static String date_format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }
}