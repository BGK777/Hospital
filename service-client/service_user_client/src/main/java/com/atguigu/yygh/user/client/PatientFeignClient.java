package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
public interface PatientFeignClient {

    /**
     * 根据id获取就诊人信息
     * @param id
     * @return
     */
    @GetMapping("/user/userinfo/patient/{id}")
    Patient getPatientById(@PathVariable Long id);
}
