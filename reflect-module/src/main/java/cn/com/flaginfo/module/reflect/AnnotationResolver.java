package cn.com.flaginfo.module.reflect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析#{}结构的值
 * #{name}
 * #{person.name}:#{person.age}
 * #{0}
 * #{toJSON(person)}
 *
 * @author meng.liu
 */
@Slf4j
public class AnnotationResolver {

    private static final String NULL_STRING = "null";

    private static final Object NULL = null;

    private static Pattern PATTERN = Pattern.compile("(#\\{([^\\}]+)\\})");

    private static final Pattern TO_JSON_PATTERN = Pattern.compile("(^toJSON\\(([^\\}]+)\\))");

    public static String resolver(String str, String[] names, Object[] args) {
        return resolver(str, names, args, NULL_STRING);
    }

    /**
     * 解析注解上的值
     *
     * @param names       参数名称
     * @param args        参数值
     * @param str         需要解析的字符串
     * @param nullReplace null对象的替代者
     * @return
     */
    public static String resolver(String str, String[] names, Object[] args, String nullReplace) {
        if (str == null) {
            return null;
        }
        Object value = null;
        Matcher matcher = PATTERN.matcher(str);
        while (matcher.find()) {
            String fParseStr = matcher.group(2).trim();
            String sParseStr = fParseStr;
            Matcher matcherJson = TO_JSON_PATTERN.matcher(fParseStr);
            boolean toJson = false;
            if (matcherJson.find()) {
                toJson = true;
                fParseStr = fParseStr.replaceAll("\\(", "\\\\(")
                        .replaceAll("\\)", "\\\\)");
                sParseStr = matcherJson.group(2).trim();
            }
            if (sParseStr.contains(".")) {
                try {
                    value = complexResolver(sParseStr, names, args);
                } catch (Exception e) {
                    log.error("", e);
                }
            } else {
                value = simpleResolver(sParseStr, names, args);
            }
            String replacement;
            if (null == value) {
                replacement = nullReplace;
            } else {
                replacement = toJson ? JSONObject.toJSONString(value) : String.valueOf(value);
            }
            if (StringUtils.isBlank(replacement)) {
                replacement = nullReplace;
            }
            str = str.replaceAll("(#\\{" + fParseStr + "\\})", Matcher.quoteReplacement(replacement));
        }
        return str;
    }

    /**
     * 解析ognl参数
     * @param str
     * @param names
     * @param args
     * @return
     */
    public static Object resolverValue(String str, String[] names, Object[] args) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        Object value = null;
        Matcher matcher = PATTERN.matcher(str);
        while (matcher.find()) {
            String sParseStr = matcher.group(2).trim();
            if (sParseStr.contains(".")) {
                try {
                    value = complexResolver(sParseStr, names, args);
                } catch (Exception e) {
                    log.error("", e);
                }
            } else {
                value = simpleResolver(sParseStr, names, args);
            }
        }
        return value;
    }

    /**
     * 递归解析数据值
     *
     * @param str
     * @param names
     * @param args
     * @return
     */
    private static Object complexResolver(String str, String[] names, Object[] args) {
        String[] strs = str.split("\\.");
        for (int i = 0; i < names.length; i++) {
            if (strs[0].equals(names[i])) {
                return getValue(args[i], 1, strs);
            }
        }
        return null;
    }

    /**
     * 递归解析数据值
     * @param str
     * @param arg
     * @return
     */
    public static Object complexResolver(String str, Object arg) {
        String[] strs = str.split("\\.");
        return getValue(arg, 0, strs);
    }

    private static Object getValue(Object obj, int position, String[] strs) {
        try {
            if (obj != null && position < strs.length) {
                if (obj instanceof Map) {
                    obj = ((Map) obj).get(strs[position]);
                } else if (obj instanceof List) {
                    obj = ((List) obj).get(Integer.valueOf(strs[position]));
                } else if (obj.getClass().isArray()) {
                    obj = Arrays.asList(obj).get(Integer.valueOf(strs[position]));
                } else {
                    obj = ReflectionUtils.invokeGetterMethod(obj, strs[position]);
                }
                obj = getValue(obj, position + 1, strs);
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 简单获取
     *
     * @param str
     * @param names
     * @param args
     * @return
     */
    private static Object simpleResolver(String str, String[] names, Object[] args) {
        if (StringUtils.isNumeric(str)) {
            return args[Integer.valueOf(str)];
        }
        for (int i = 0; i < names.length; i++) {
            if (str.equals(names[i])) {
                return args[i];
            }
        }
        return null;
    }
}
