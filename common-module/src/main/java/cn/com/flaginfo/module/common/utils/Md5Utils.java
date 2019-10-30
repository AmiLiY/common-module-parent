package cn.com.flaginfo.module.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @Author: LiuMeng
 * @Describe:
 * @Time: 2018/10/29 15:53
 */
@Slf4j
public class Md5Utils {

    public static String encrypt(String str) throws Exception {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return encrypt(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(byte[] bytes) throws Exception {
        if ( null == bytes || 0 == bytes.length) {
            return null;
        }
        MessageDigest md5 = MessageDigest.getInstance("md5");
        md5.update(bytes);
        byte[] md5Bytes = md5.digest();
        return Base64.encodeBase64String(md5Bytes);
    }

}
