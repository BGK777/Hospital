package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WeiPayServiceImpl implements WeiPayService {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private RefundInfoService refundInfoService;

    @Override
    public String getUrl(Long orderId) {
        //获取订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        //往支付记录里保存记录
        paymentInfoService.saveOrderByIdAndType(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        //请求微信服务器获取二维码地址
        //返回微信支付url
        return new String("https://www.bilibili.com");
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {

        Map<String, String> stringStringMap = new HashMap<>();
        //做向微信平台查询订单是否成功的功能，但微信支付现在为V3版本个人无法实践测试
        //TODO
        //直接成功
        stringStringMap.put("trade_state","SUCCESS");

        return stringStringMap; //支付

    }

    @Transactional
    @Override
    public void paySuccess(Long orderId, Map<String,String> map) {
        //更新订单表的订单状态
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        //更新支付记录表的支付状态
        UpdateWrapper<PaymentInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderId);
        updateWrapper.set("trade_no", UUID.randomUUID().toString()); //微信支付的订单号[微信服务器]
        updateWrapper.set("payment_status", PaymentStatusEnum.PAID.getStatus());
        updateWrapper.set("callback_time",new Date());
        updateWrapper.set("callback_content", "算是成功了吧");
        paymentInfoService.update(updateWrapper);
    }

    @Override
    public boolean refund(Long orderId) {
        //先根据订单id查询支付记录
        LambdaQueryWrapper<PaymentInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PaymentInfo::getOrderId,orderId);
        PaymentInfo paymentInfo = paymentInfoService.getOne(lqw);

        //在调用退款接口
        RefundInfo refundInfo = refundInfoService.saveRefund(paymentInfo);

        //已退款
        if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()){
            return true;
        }
        //没有做微信退款操作，直接修改退款记录表
        refundInfo.setTradeNo(UUID.randomUUID().toString());//微信退款交易号
        refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
        refundInfo.setCallbackTime(new Date());
        refundInfo.setCallbackContent("退款成功了！");
        refundInfoService.updateById(refundInfo);
        return true;
    }
}
