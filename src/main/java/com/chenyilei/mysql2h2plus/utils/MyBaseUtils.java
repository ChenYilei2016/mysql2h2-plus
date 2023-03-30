package com.chenyilei.mysql2h2plus.utils;

import com.intellij.util.ReflectionUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MyBaseUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || !containsText(str);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String currentTime(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static String currentTime() {
        return currentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 转驼峰命名正则匹配规则
     */
    private static final Pattern TO_HUMP_PATTERN = Pattern.compile("[-_]([a-z0-9])");

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    /**
     * 首字母大写方法
     *
     * @param name 名称
     * @return 结果
     */
    public static String firstUpperCase(String name) {
        return changeFirstCharacterCase(name, true);
    }

    /**
     * 首字母小写方法
     *
     * @param name 名称
     * @return 结果
     */
    public static String firstLowerCase(String name) {
        return changeFirstCharacterCase(name, false);
    }

    /**
     * 通过java全名获取类名
     *
     * @param fullName 全名
     * @return 类名
     */
    public static String getClsNameByFullName(String fullName) {
        return fullName.substring(fullName.lastIndexOf('.') + 1, fullName.length());
    }

    /**
     * 下划线中横线命名转驼峰命名（属性名）
     *
     * @param name 名称
     * @return 结果
     */
    public static String getJavaName(String name) {
        if (MyBaseUtils.isEmpty(name)) {
            return name;
        }
        // 强转全小写
        name = name.toLowerCase();
        Matcher matcher = TO_HUMP_PATTERN.matcher(name.toLowerCase());
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 下划线中横线命名转驼峰命名（类名）
     *
     * @param name 名称
     * @return 结果
     */
    public static String getClassName(String name) {
        return firstUpperCase(getJavaName(name));
    }

    /**
     * 任意对象合并工具类
     *
     * @param objects 任意对象
     * @return 合并后的字符串结果
     */
    public static String append(Object... objects) {

        if (MyBaseUtils.isEmpty(objects)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object s : objects) {
            if (s != null) {
                builder.append(s);
            }
        }
        return builder.toString();
    }

    /**
     * 创建集合
     *
     * @param items 初始元素
     * @return 集合对象
     */
    public static Set<?> newHashSet(Object... items) {
        return items == null ? new HashSet<>() : new HashSet<>(Arrays.asList(items));
    }

    /**
     * 创建列表
     *
     * @param items 初始元素
     * @return 列表对象
     */
    public static List<?> newArrayList(Object... items) {
        return items == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
    }

    /**
     * 创建有序Map
     *
     * @return map对象
     */
    public static Map<?, ?> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * 创建无须Map
     *
     * @return map对象
     */
    public static Map<?, ?> newHashMap() {
        return new HashMap<>(16);
    }

    /**
     * 获取字段，私有属性一样强制访问
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getField(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }
        Class<?> cls = obj.getClass();
        return ReflectionUtil.getField(cls, obj, Object.class, fieldName);
    }

    /**
     * 无返回执行，用于消除返回值
     *
     * @param obj 接收执行返回值
     */
    public static void call(Object... obj) {

    }

    /**
     * 获取某个类的所有字段
     *
     * @param cls 类
     * @return 所有字段
     */
    private static List<Field> getAllFieldByClass(Class<?> cls) {
        List<Field> result = new ArrayList<>();
        do {
            result.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        } while (!cls.equals(Object.class));
        return result;
    }




    /**
     * 生成长度为18位的序列号，保持代码美观
     *
     * @return 序列化
     */
    public static String serial() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        // 正负号生成
        if (random.nextFloat() > 0.5F) {
            builder.append("-");
        }
        // 首位不能为0
        builder.append(random.nextInt(9) + 1);
        // 生成剩余位数
        do {
            builder.append(random.nextInt(10));
        } while (builder.length() < 18);
        // 加上结束符号
        builder.append("L");
        return builder.toString();
    }

    public static String getOsUser() {
        //windows
        String username = System.getenv("USERNAME");
        if (isEmpty(username)) {
            //linux
            username = System.getenv("USER");
        }
        if (isEmpty(username)){
            username = "";
        }
        return username;
    }

    public static InputStream readFile(String filepath) throws IOException {
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
}
