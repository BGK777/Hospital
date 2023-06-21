package com.atguigu.yygh.user.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface WxApiService {
    String callback(String code, String state) throws Exception;

    Map<String, Object> getConnect() throws UnsupportedEncodingException;

}
