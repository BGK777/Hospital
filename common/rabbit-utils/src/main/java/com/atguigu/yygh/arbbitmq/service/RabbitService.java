package com.atguigu.yygh.arbbitmq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 根据传入交换机，路由，信息发送mq信息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     * @return
     */
    public boolean sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }
}
