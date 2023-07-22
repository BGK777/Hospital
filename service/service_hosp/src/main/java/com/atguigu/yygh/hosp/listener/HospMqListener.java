package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.arbbitmq.MQEnums.MqConst;
import com.atguigu.yygh.arbbitmq.service.RabbitService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class HospMqListener {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private RabbitService rabbitService;


    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_ORDER), //创建队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER), //创建交换机
                    key = MqConst.ROUTING_ORDER) //路由键
    })
    public void consumer(OrderMqVo orderMqVo, Message message, Channel channel){
        if(orderMqVo == null){
            return;
        }
        //更改排班信息中的可预约数
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(availableNumber != null){
            scheduleService.updateAvailableNumber(scheduleId,availableNumber);
        }else {
            scheduleService.cancel(scheduleId);
        }
        //如果成功则向msm模块发送mq消息，发送就医提醒或取消预约提醒
        if(msmVo != null){
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM,MqConst.ROUTING_MSM_ITEM,msmVo);
        }
    }
}
