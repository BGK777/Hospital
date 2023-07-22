package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hosp")
public interface HospitalFeignClient {

    /**
     * 根据scheduleId获取排班信息
     * @return
     */
    @GetMapping("/user/hosp/schedule/{scheduleId}")
    ScheduleOrderVo getScheduleOrderById(@PathVariable("scheduleId") String scheduleId);
}
