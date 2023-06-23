package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.result.R;
import com.baomidou.mybatisplus.extension.api.ApiController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单表(OrderInfo)表控制层
 *
 * @author makejava
 * @since 2023-06-23 15:47:15
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController extends ApiController {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 根据排班id，就诊人id封装order并保存信息
     * @param scheduleId
     * @param patientId
     * @return
     */
    @PostMapping("/{scheduleId}/{patientId}")
    public R submitOrder(@PathVariable("scheduleId") String scheduleId,@PathVariable("patientId") Long patientId){
        Long orderId = orderInfoService.submitOrder(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }

}

