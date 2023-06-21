package com.atguigu.yygh.hosp.utils;

import com.atguigu.yygh.common.utils.MD5;
import org.springframework.util.StringUtils;

/**
 * 验证key
 */
public class SignKeyHelper {
    /**
     * 验证 SignKey
     * @param requestSignKey
     * @param platformSignKey
     * @return
     */
    public static boolean Helper(String requestSignKey,String platformSignKey){
        String encrypt = MD5.encrypt(platformSignKey);
        return !StringUtils.isEmpty(requestSignKey) && !StringUtils.isEmpty(encrypt) && encrypt.equals(requestSignKey);
    }
}
