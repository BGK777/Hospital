package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.extension.api.ApiController;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询订单列表
     * @param pageNum
     * @param pageSize
     * @param orderQueryVo
     * @param token
     * @return
     */
    @GetMapping("/{pageNum}/{pageSize}")
    public R getOrderInfoPage(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              OrderQueryVo orderQueryVo,
                              @RequestHeader String token){

        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo>  page= orderInfoService.getOrderInfoPage(pageNum,pageSize,orderQueryVo);


        return R.ok().data("page",page);
    }

    /**
     * 获取订单状态列表
     * @return
     */
    @GetMapping("/list")
    public R getOrderList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("list",statusList);
    }

    /**
     * 根据id获取订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/{orderId}")
    public R detail(@PathVariable Long orderId){
        OrderInfo orderInfo = orderInfoService.detail(orderId);
        return R.ok().data("orderInfo",orderInfo);
    }

    /**
     * 取消预约
     * @param orderId
     * @return
     */
    @GetMapping("cancel/{orderId}")
    public R cancel(@PathVariable("orderId") Long orderId){
        orderInfoService.cancel(orderId);
        return R.ok();
    }
}

