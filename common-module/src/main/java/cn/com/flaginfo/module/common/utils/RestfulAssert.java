package cn.com.flaginfo.module.common.utils;

import cn.com.flaginfo.exception.Error;
import cn.com.flaginfo.exception.restful.RestfulException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: LiuMeng
 * @date: 2019/8/15
 * TODO:
 */
public class RestfulAssert {

    public static void hasText(final CharSequence cs, String message) throws RestfulException{
        isTrue(StringUtils.isNotBlank(cs), message);
    }

    public static void hasText(final CharSequence cs, Error error) throws RestfulException{
        isTrue(StringUtils.isNotBlank(cs), error);
    }

    public static void hasText(final CharSequence cs, long code, String message) throws RestfulException{
        isTrue(StringUtils.isNotBlank(cs), code, message);
    }

    public static void isEmpty(final CharSequence cs, String message) throws RestfulException{
        isTrue(StringUtils.isEmpty(cs), message);
    }

    public static void isEmpty(final CharSequence cs, Error error) throws RestfulException{
        isTrue(StringUtils.isEmpty(cs), error);
    }

    public static void isEmpty(final CharSequence cs, long code, String message) throws RestfulException{
        isTrue(StringUtils.isEmpty(cs), code, message);
    }

    public static void isBlank(final CharSequence cs, String message) throws RestfulException{
        isTrue(StringUtils.isBlank(cs), message);
    }

    public static void isBlank(final CharSequence cs, Error error) throws RestfulException{
        isTrue(StringUtils.isBlank(cs), error);
    }

    public static void isBlank(final CharSequence cs, long code, String message) throws RestfulException{
        isTrue(StringUtils.isBlank(cs), code, message);
    }

    public static void isNotNull(Object o, String message) throws RestfulException {
        isTrue(o != null, message);
    }

    public static void isNotNull(Object o, Error error) throws RestfulException {
        isTrue(o != null, error);
    }

    public static void isNotNull(Object o, long code, String message) throws RestfulException {
        isTrue(o != null, code, message);
    }

    public static void isNull(Object o, String message) throws RestfulException {
        isTrue(o == null, message);
    }

    public static void isNull(Object o, Error error) throws RestfulException {
        isTrue(o == null, error);
    }

    public static void isNull(Object o, long code, String message) throws RestfulException {
        isTrue(o == null, code, message);
    }

    public static void isFalse(boolean expression, String message) throws RestfulException {
        isFalse(!expression, 500, message);
    }

    public static void isFalse(boolean expression, long code, String message) throws RestfulException {
        if (expression) {
            throw new RestfulException(code, message);
        }
    }

    public static void isFalse(boolean expression, Error error) throws RestfulException {
        isFalse(!expression, error.code(), error.message());
    }

    public static void isTrue(boolean expression, String message) throws RestfulException {
        isTrue(expression, 500, message);
    }

    public static void isTrue(boolean expression, Error error) throws RestfulException {
        isTrue(expression, error.code(), error.message());
    }

    public static void isTrue(boolean expression, long code, String message) throws RestfulException {
        if (!expression) {
            throw new RestfulException(code, message);
        }
    }
}
