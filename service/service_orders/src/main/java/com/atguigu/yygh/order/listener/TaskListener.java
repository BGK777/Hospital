package com.atguigu.yygh.order.listener;

import com.atguigu.yygh.arbbitmq.MQEnums.MqConst;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TaskListener {

    @Resource
    private OrderInfoService orderInfoService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_TASK_8),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_TASK),
                    key = MqConst.ROUTING_TASK_8)
    })
    public void consumer(Message message, Channel channel){
        orderInfoService.patientRemind();
    }
}
