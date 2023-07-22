package com.atguigu.yygh.task.job;

import com.atguigu.yygh.arbbitmq.MQEnums.MqConst;
import com.atguigu.yygh.arbbitmq.service.RabbitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PatientRemindJob {

    @Resource
    private RabbitService rabbitService;

    /**
     * 定时任务每天6点给MQ发消息
     */
//    @Scheduled(cron = "0 0 6 * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void remindPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8," ");
    }
}
