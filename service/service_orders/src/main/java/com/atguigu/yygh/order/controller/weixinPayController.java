package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.service.impl.WeiPayServiceImpl;
import com.atguigu.yygh.result.R;
import com.baomidou.mybatisplus.extension.api.ApiController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 支付信息表(PaymentInfo)表控制层
 *
 * @author makejava
 * @since 2023-06-25 16:08:52
 */
@RestController
@RequestMapping("user/order/weixin")
public class weixinPayController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private WeiPayService weiPayService;


    /**
     * 生成微信支付地址给前端生成二维码
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public R getUrl(@PathVariable("orderId") Long orderId){
        String url =weiPayService.getUrl(orderId);
        return R.ok().data("url",url);
    }

    @GetMapping("/status/{orderId}")
    public R getPayStatus(@PathVariable Long orderId){
        Map<String,String> map=weiPayService.queryPayStatus(orderId);
        if(map == null){
            return R.error().message("查询失败");
        }
        //查询成功+支付成功
        if("SUCCESS".equals(map.get("trade_state"))){ //支付成功
            weiPayService.paySuccess(orderId,map);//更新了订单状态0 1 +支付记录表的支付状态:1 2
            return R.ok();
        }
        //
        return R.ok().message("支付中"); //支付失败
    }
}

