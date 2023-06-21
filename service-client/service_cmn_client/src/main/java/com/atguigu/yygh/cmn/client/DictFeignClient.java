package com.atguigu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 数据字典API接口
 */
@FeignClient(value = "service-cmn")
public interface DictFeignClient {

    /**
     * 根据value获取数据字典名字
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/getName/{value}")
    String getName(@PathVariable("value") String value);

    /**
     * 根据praentDictCode和value获取数据字典名字
     * @param parentDictCode
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/getName/{parentDictCode}/{value}")
    String getName(@PathVariable("parentDictCode") String parentDictCode, @PathVariable("value") String value);
}
