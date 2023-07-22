package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 退款信息表(RefundInfo)表服务接口
 *
 * @author makejava
 * @since 2023-07-21 18:02:51
 */
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefund(PaymentInfo paymentInfo);
}
