package com.atguigu.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;

public class MapHelper {
    /**
     * 把Map<String, String[]> 转为  Map<String, Object>
     * @param parameterMap
     * @return
     */
    public static Map<String, Object> Helper(Map<String, String[]> parameterMap) {
        Map<String, Object> res = new HashMap<>();
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            res.put(param.getKey(),param.getValue()[0]);
        }
        return res;
    }
}
