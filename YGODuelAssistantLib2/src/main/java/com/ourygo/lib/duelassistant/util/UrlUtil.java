package com.ourygo.lib.duelassistant.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Create By feihua  On 2021/9/29
 */
public class UrlUtil {

    /**
     * URLEncoder编码
     */
    public static String enURL(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), StandardCharsets.UTF_8);
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
        }
        return "";
    }

    /**
     * URLDecoder解码
     */
    public static String deURL(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String url = new String(paramString.getBytes(), StandardCharsets.UTF_8);
            url = URLDecoder.decode(url, "UTF-8");
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
