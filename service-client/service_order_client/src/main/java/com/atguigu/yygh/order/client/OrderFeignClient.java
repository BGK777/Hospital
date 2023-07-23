package com.atguigu.yygh.order.client;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "service-orders")
public interface OrderFeignClient {
    /**
     * 预约统计
     * @param orderCountQueryVo
     * @return
     */
    @PostMapping("/api/order/orderInfo/statistic")
    Map<String,Object> statistic(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
