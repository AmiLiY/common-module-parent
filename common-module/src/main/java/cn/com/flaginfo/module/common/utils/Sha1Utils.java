package cn.com.flaginfo.module.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;

/**
 * @author LiuMeng
 * @version 1.0
 * @className Sha1Utils
 * @describe TODO
 * @date 2019/7/19 18:54
 */
public class Sha1Utils {

    public static String encrypt(String str) throws Exception {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        // SHA1签名生成
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(str.getBytes());
        byte[] digest = md.digest();

        StringBuffer hexstr = new StringBuffer();
        String shaHex = "";
        for (int i = 0; i < digest.length; i++) {
            shaHex = Integer.toHexString(digest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexstr.append(0);
            }
            hexstr.append(shaHex);
        }
        return hexstr.toString();
    }

}
