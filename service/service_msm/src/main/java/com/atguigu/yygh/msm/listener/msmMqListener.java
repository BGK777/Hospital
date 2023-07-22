package com.atguigu.yygh.msm.listener;

import com.atguigu.yygh.arbbitmq.MQEnums.MqConst;
import com.atguigu.yygh.arbbitmq.service.RabbitService;
import com.atguigu.yygh.msm.service.MsmService;
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
public class msmMqListener {
    @Resource
    private MsmService msmService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_MSM_MSM),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_MSM),
                    key = MqConst.ROUTING_MSM_ITEM
            )
    })
    public void consumer(MsmVo msmVo, Message message, Channel channel){
        msmService.sendMessage(msmVo);
    }
}
