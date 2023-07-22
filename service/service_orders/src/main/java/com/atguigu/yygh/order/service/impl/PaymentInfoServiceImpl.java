package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.mapper.PaymentInfoMapper;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * 支付信息表(PaymentInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-06-25 16:08:52
 */
@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Override
    public void saveOrderByIdAndType(OrderInfo orderInfo, Integer weixin) {
        //判断支付表里是否有记录，如果有记录不保存
        LambdaQueryWrapper<PaymentInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PaymentInfo::getOrderId,orderInfo.getId());
        PaymentInfo paymentInfo1 = baseMapper.selectOne(lqw);
        if(paymentInfo1 == null){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentType(weixin);
            paymentInfo.setOrderId(orderInfo.getId());
            paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
            paymentInfo.setTotalAmount(orderInfo.getAmount());

            String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
            paymentInfo.setSubject(subject);
            paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus()); //支付状态
            baseMapper.insert(paymentInfo);
        }
    }
}
