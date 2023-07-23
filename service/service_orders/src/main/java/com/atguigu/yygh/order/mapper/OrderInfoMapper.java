package com.atguigu.yygh.order.mapper;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * 订单表(OrderInfo)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-23 15:47:15
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> statistic(OrderCountQueryVo orderCountQueryVo);
}
