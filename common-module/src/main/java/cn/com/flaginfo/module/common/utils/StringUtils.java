package cn.com.flaginfo.module.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private final static Pattern PATTERN = Pattern.compile("[A-Z]");

    /**
     * 驼峰字符串转下划线连接的字符串
     * @param camelsString
     * @return
     */
    public static String camels2Underline(String camelsString){
        return camelsFormatter(camelsString, StringPool.UNDERSCORE);
    }


    /**
     * 驼峰字符串转中划线连接的字符串
     * @param camelsString
     * @return
     */
    public static String camels2Strikethrough(String camelsString){
        return camelsFormatter(camelsString, StringPool.DASH);
    }

    /**
     * 驼峰字符串转自定义连接器拼接的字符串
     * @param camelsString
     * @param connector
     * @return
     */
    public static String camelsFormatter(String camelsString, CharSequence connector){
        if( isBlank(camelsString) ){
            return camelsString;
        }
        if( isEmpty(connector) ){
            return camelsString;
        }
        Matcher matcher = PATTERN.matcher(camelsString);
        StringBuffer strBuffer = new StringBuffer();
        while ( matcher.find() ){
            matcher.appendReplacement(strBuffer, connector + matcher.group().toLowerCase());
        }
        matcher.appendTail(strBuffer);
        return strBuffer.toString();
    }

    /**
     * 自定义连接符拼接字符串转驼峰字符串
     * @param jointString
     * @param connector
     * @return
     */
    public static String connectorJoint2Camels(String jointString, CharSequence connector){
        if( isBlank(jointString) ){
            return jointString;
        }
        if( isEmpty(connector) ){
            return jointString;
        }
        char[] strChars = jointString.toCharArray();
        char[] connectorChars = connector.toString().toCharArray();
        char[] cacheStrChars = new char[strChars.length];
        int position = 0;
        boolean flag;
        boolean nextToUp = false;
        for( int s = 0; s < strChars.length;  ){
            if( strChars[s] != connectorChars[0]  ){
                if( nextToUp && (strChars[s] >= 'a' && strChars[s] <= 'z')){
                    cacheStrChars[position++] = (char)(strChars[s] - 32);
                    nextToUp = false;
                }else{
                    cacheStrChars[position++] = strChars[s];
                }
                s++;
            }else{
                flag = false;
                for( int c = 0; c < connectorChars.length; c++ ){
                    if( s + c >= strChars.length - 1 ){
                        flag = false;
                        break;
                    }else{
                        if( connectorChars[c] == strChars[s + c] ){
                            flag = true;
                        }else{
                            flag = false;
                            break;
                        }
                    }
                }
                if( flag ){
                    nextToUp = true;
                    s += connectorChars.length;
                }else{
                    nextToUp = false;
                    cacheStrChars[position++] = strChars[s];
                    s++;
                }
            }
        }
        char[] newStrChars = new char[position - 1];
        System.arraycopy(cacheStrChars, 0, newStrChars, 0, position - 1 );
        return new String(newStrChars);
    }
}
