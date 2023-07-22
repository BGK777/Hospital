package com.atguigu.yygh.order.service;

import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 支付信息表(PaymentInfo)表服务接口
 *
 * @author makejava
 * @since 2023-06-25 16:08:52
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    void saveOrderByIdAndType(OrderInfo orderInfo, Integer weixin);
}
