package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 订单表(OrderInfo)表服务接口
 *
 * @author makejava
 * @since 2023-06-23 15:47:16
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long submitOrder(String scheduleId, Long patientId);
}
