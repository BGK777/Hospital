package com.atguigu.yygh.statistics.service;

import com.atguigu.yygh.order.client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class StatisticsService {

    @Resource
    private OrderFeignClient orderFeignClient;

    public Map<String, Object> statistic(OrderCountQueryVo orderCountQueryVo) {
        return orderFeignClient.statistic(orderCountQueryVo);
    }
}
