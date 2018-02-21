package com.terran4j.commons.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.terran4j.commons.util.value.ValueSource;

/**
 * 字符串操作工具类。
 * 
 * @author wei.jiang
 */
public class Strings {
    
    private static final Logger log = LoggerFactory.getLogger(Strings.class);

    /**
     * 字节数据转字符串专用集合
     */
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取对象的描述信息。
     * 
     * @param value 具体对象。
     * @return 对象的字符串信息(按json串的语法描述)。
     */
    public static final String toString(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return Jsons.getObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 按一个异常对象按成字符串的描述输出。
     * 
     * @param t 异常对象
     * @return 异常堆栈的文本内容。
     */
    public static String getString(Throwable t) {
        if (t == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PrintWriter pw = new PrintWriter(out);
            t.printStackTrace(pw);
            pw.flush();
            String str = new String(out.toByteArray(), Encoding.UTF8.getName());
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                // ignore the error.
            }
        }
    }

    /**
     * 从classpath中读取文本资源文件。<br>
     * 比如：包 com.terran4j.demo3 下有一个类： Hello.java 和一个 hi.txt 文件：<br>
     * 那 getString(Hello.class, "hi.txt") 方法可以获取 hi.txt 文件的内容。
     * 
     * @param clazz 与文本文件在相同包下的类。
     * @param fileName 广西文件的名称。
     * @return 文件中的字符串。
     */
    public static String getString(Class<?> clazz, String fileName) {
        String path = null;
        ClassLoader loader = null;
        if (clazz == null) {
            path = fileName;
            loader = Strings.class.getClassLoader();
        } else {
            path = getClassPath(clazz, fileName);
            loader = clazz.getClassLoader();
        }

        return getResourceByPath(path, loader);
    }

    public static final String getResourceByPath(String path, ClassLoader loader) {
        InputStream in = loader.getResourceAsStream(path);
        if (in == null) {
            return null;
        }

        try {
            String str = getString(in);
            if (str == null) {
                str = "";
            }
            return str;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 将字符串转化成一个输入流对象。
     * 
     * @param str 字符串
     * @return 对应的输入流
     */
    public static InputStream toInputStream(String str) {
        if (str == null) {
            return null;
        }

        try {
            InputStream in = new ByteArrayInputStream(str.getBytes(Encoding.UTF8.getName()));
            return in;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从输入流中读取出所有内容，并按字符串返回。
     * 
     * @param in 输入流
     * @return 文本内容。
     */
    public static String getString(InputStream in) {
        return getString(in, Encoding.getDefaultEncoding());
    }

    /**
     * 从输入流中读取出所有内容，并按字符串返回内容，并且可以指定字符串编码方式。
     * 
     * @param in 输入流
     * @param encoding 字符串编码方式
     * @return 字符串内容
     */
    public static String getString(InputStream in, Encoding encoding) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine() method. We
         * iterate until the BufferedReader return null which means there's no more data to read.
         * Each line will appended to a StringBuilder and returned as String.
         */
        StringBuilder sb = new StringBuilder();
        try {
            if (encoding == null) {
                encoding = Encoding.getDefaultEncoding();
            }
            InputStreamReader inr = new InputStreamReader(in, encoding.getName());
            BufferedReader reader = new BufferedReader(inr);

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // #1005 去掉in.close()语句。

        return sb.toString();

    }

    /**
     * 根据类对象， 获取同包下的文件的资源路径。
     * 
     * @param clazz 类对象
     * @param fileName 与类同包下的文件名
     * @return 文件中的字符串内容。
     */
    public static String getClassPath(final Class<?> clazz, String fileName) {
        Package classPackage = clazz.getPackage();
        if (classPackage != null) {
            return classPackage.getName().replace('.', '/') + "/" + fileName;
        } else {
            return fileName;
        }
    }

    /**
     * 将一个带变量的文本内容（后续称之为“模板内容”），
     * 按给定的变量值格式化为具体的文本内容。<br>
     * 模板内容可以用 ${ 与 } 包裹起来作为变量。<br>
     * 比如：<br>
     * 模板内容为：str = "尊敬的${name}您好，XXXXX";<br>
     * 变量值为： args = {"name": "terran4j"};<br>
     * 调用 <code>format(str, args)</code> 方法后，返回的内容为：<br>
     * 尊敬的terran4j您好，XXXXX
     * 
     * @param str 模板内容。
     * @param args 变量值。
     * @param nullAsEmpty 如果找不到变量，按空串 "" 来替换。
     * @return 格式化之后的具体文本内容。
     */
    public static String format(String str, final Map<String, Object> args, final boolean nullAsEmpty) {
        ValueSource<String, String> values = new ValueSource<String, String>() {

            @Override
            public String get(String key) {
                Object value = null;
                if (args != null) {
                    value = args.get(key);
                }
                if (nullAsEmpty && value == null) {
                    value = "";
                }
                return Objects.toString(value);
            }

        };
        return format(str, values, "${", "}", null);
    }

    public static String format(String str, final Map<String, Object> args) {
        return format(str, args, false);
    }

    /**
     * 与 format(String str, final Map args) 方法作用一样，
     * 唯一的区别是变量值是从<code>ValueSource</code>对象中取。<br>
     * 
     * @param str 模板内容。
     * @param values 变量值。
     * @return 格式化之后的具体文本内容。
     */
    public static String format(String str, ValueSource<String, String> values) {
        return format(str, values, "${", "}", null);
    }

    /**
     * 与 format(String str, final Map args) 方法作用一样， 但可以更灵活可定制化，
     * 区别是: <br>
     * 1. 变量值是从<code>ValueSource</code>对象中取。<br>
     * 2. 可以用 begin 和 end 来自定义变量在模板内容中的表达格式，如：
     * begin = "#[", end = "]" 表示变量是 #[name] 这类的格式，而不是传统的 ${name} 。<br>
     * 3. 可以收集哪些变量没有被替换掉的，没有被替换掉的被放在一个叫
     * notMatched 的List类型的参数中。<br>
     * 
     * @param str 模板内容。
     * @param values 变量值。
     * @param begin 变量包裹的起始符。
     * @param end 变量包裹的结束符。
     * @param notMatched 被替换掉的变量的key.
     * @return 格式化之后的具体文本内容。
     */
    public static String format(String str, ValueSource<String, String> values, String begin, String end,
            List<String> notMatched) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }

        if (begin == null || begin.trim().length() == 0 || end == null || end.trim().length() == 0) {
            throw new NullPointerException("begin or end is null or empty：" + begin + ", " + end);
        }

        StringBuffer sb = new StringBuffer();
        final int size = str.length();
        final int beginLength = begin.length();
        final int endLength = end.length();
        int from = 0;
        while (true) {
            int m = str.indexOf(begin, from);
            int n = str.indexOf(end, from);

            if (m >= 0 && m < size && n > m && n < size) {
                String s0 = str.substring(from, m);
                sb.append(s0);
                String sMatch = str.substring(m, n + endLength);
                String key = sMatch.substring(beginLength, sMatch.length() - endLength);
                String value = values.get(key);
                if (value != null) {
                    sb.append(value);
                } else {
                    sb.append(sMatch);
                    if (notMatched != null) {
                        notMatched.add(key);
                    }
                }
                from = n + endLength;
            } else {
                break;
            }
        }
        if (from < size) {
            sb.append(str.substring(from));
        }

        return sb.toString();
    }

    /**
     * 按空格分割字符串成数组，会对每个数组元素进行 trim 操作，并丢弃掉为“空串”的元素。
     * 
     * @param content 字符串内容。
     * @return trim后的字符串数组。
     */
    public static String[] splitWithTrim(String content) {
        return splitWithTrim(content, -1);
    }

    /**
     * 按指定的表达式 regex 进行分割字符串成数组，会对每个数组元素进行 trim 操作，并丢弃掉为“空串”的元素。
     * 
     * @param content 字符串内容。
     * @param regex 指定的表达式
     * @return trim后的字符串数组。
     */
    public static String[] splitWithTrim(String content, String regex) {
        return splitWithTrim(content, regex, -1);
    }

    /**
     * 按空格分割字符串成数组，会对每个数组元素进行 trim 操作，并丢弃掉为“空串”的元素。<br>
     * 与<code>splitWithTrim(String content)</code>方法不同的时，可以指定分割最大数量。
     * 
     * @param content 字符串内容。
     * @param limit 分割最大数量
     * @return trim后的字符串数组。
     */
    public static String[] splitWithTrim(String content, int limit) {
        if (content == null) {
            return null;
        }
        return splitWithTrim(content, " ", limit);
    }

    /**
     * 按指定的表达式 regex 进行分割字符串成数组，会对每个数组元素进行 trim 操作，并丢弃掉为“空串”的元素。<br>
     * 可以指定分割最大数量。<br>
     * 比如：content = "abc xxx xxxdef xxx ghi", regex = "xxx", limit = 2; <br>
     * 返回的结果为： ["abc", "def xxx ghi"]。
     * 
     * @param content 字符串内容。
     * @param regex 指定的表达式
     * @param limit 分割最大数量
     * @return trim后的字符串数组。
     */
    public static String[] splitWithTrim(String content, String regex, int limit) {
        if (content == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        String[] strs = content.split(regex, limit);
        if (strs != null) {
            for (String str : strs) {
                str = str.trim();
                if (StringUtils.isEmpty(str)) {
                    continue;
                }
                result.add(str);
            }
        }
        if (limit >= 0 && result.size() < limit) {
            int leftLimit = limit - result.size();
            int lastIndex = result.size() - 1;
            String lastContent = result.get(lastIndex);
            String[] leftStrs = splitWithTrim(lastContent, regex, leftLimit);
            if (leftStrs != null && leftStrs.length > 0) {
                result.remove(lastIndex);
                result.addAll(Arrays.asList(leftStrs));
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * 将字节数组转成16进制描述的字符串。
     * 
     * @param data 字节数组
     * @return 16进制描述的字符串
     */
    public static String toHexString(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            // 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            sb.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
            // 取出字节的低四位 作为索引得到相应的十六进制标识符
            sb.append(HEX_CHAR[(data[i] & 0x0f)]);
        }
        return sb.toString();
    }

    /**
     * 将字节数组转成16进制描述的字符串。
     * 
     * @param str 字节数组
     * @return 16进制描述的字符串
     */
    public static byte[] fromHexString(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        str = str.trim().toLowerCase();

        if (str.length() % 2 == 1) {
            throw new InvalidParameterException("无效的16进制字符串：" + str);
        }
        byte[] data = new byte[str.length() / 2];

        for (int n = 0, i = 0; i < str.length() - 1; n++, i = i + 2) {
            int lowValue = hexToNumber(i + 1, str);
            int highValue = hexToNumber(i, str);
            int value = (highValue * 16) + lowValue;
            data[n] = (byte) value;
        }
        return data;
    }

    private static final int hexToNumber(int i, String str) {
        char c = str.charAt(i);
        int value = (c >= 'a') ? (c - 'a' + 10) : (c - '0');
        if (value < 0 || value > HEX_CHAR.length - 1) {
            String msg = String.format("无效的16进制字符串：%s, 第%d个字符不合法: %s", str, i + 1, c);
            throw new InvalidParameterException(msg);
        }
        return value;
    }

    public static final String toString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(bytes[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 检测指定的 path 是否匹配 regexPaths 。
     * 
     * @param path 路径。
     * @param regexPaths 需要匹配的路径。
     * @return 匹配的路径。
     */
    public static boolean match(String path, String... regexPaths) {
        PathMatcher pathMatch = new AntPathMatcher();
        if (path == null || regexPaths == null) {
            return false;
        }
        for (String regexPath : regexPaths) {
            if (regexPath == null) {
                continue;
            }
            regexPath = regexPath.trim();
            path = path.trim();
            if (pathMatch.match(regexPath, path)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 将字符串分割成 Map 形式。
     * @param content 字符串内容
     * @param split 分割符
     * @param joiner key与value之间的连接符。
     * @return map对象。
     */
    public static Map<String, String> toMap(String content, String split, String joiner) {
        String[] array = splitWithTrim(content, split);
        if (array == null || array.length == 0) {
            return null;
        }
        
        Map<String, String> result = new HashMap<String, String>();
        for (String item : array) {
            if (item == null || item.trim().length() == 0) {
                continue;
            }
            int i = item.indexOf(joiner);
            if (i <= 0 || i > item.length() - 1) {
                if (log.isWarnEnabled()) {
                    log.warn("unresolve[" + item + "] " + "for experssion: " + item);
                }
            }
            String key = item.substring(0, i).trim();
            String value = item.substring(i + joiner.length()).trim();
            result.put(key, value);
        }
        return result;
    }

}
