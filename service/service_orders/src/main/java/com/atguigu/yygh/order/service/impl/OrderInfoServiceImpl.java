package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * 订单表(OrderInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-06-23 15:47:16
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private HospitalFeignClient hospitalFeignClient;

    @Resource
    private PatientFeignClient patientFeignClient;

    @Override
    public Long submitOrder(String scheduleId, Long patientId) {
        //1.获取排班信息和，就诊人信息
        Patient patientById = patientFeignClient.getPatientById(patientId);
        ScheduleOrderVo scheduleById = hospitalFeignClient.getScheduleById(scheduleId);


        //判断当前时间是否已经超过今天的预约挂号截止时间
        if(new DateTime(scheduleById.getStopTime()).isBeforeNow()){
            throw new YyghException(20001,"已过挂号时间");
        }


        //2.从平台请求第三方医院，确认当前用户能否挂号
        //封装请求第三方医院参数
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",scheduleById.getHoscode());
        paramMap.put("depcode",scheduleById.getDepcode());
        paramMap.put("hosScheduleId",scheduleById.getHosScheduleId());

        paramMap.put("reserveDate",scheduleById.getReserveDate());
        paramMap.put("reserveTime",scheduleById.getReserveTime());
        paramMap.put("amount",scheduleById.getAmount());


        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if(jsonObject != null && jsonObject.getInteger("code") == 200){
            JSONObject data = jsonObject.getJSONObject("data");


            //封装订单对象
            OrderInfo orderInfo=new OrderInfo();
            orderInfo.setUserId(patientById.getUserId());
            String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setHoscode(scheduleById.getHoscode());
            orderInfo.setHosname(scheduleById.getHosname());
            orderInfo.setDepcode(scheduleById.getDepcode());
            orderInfo.setDepname(scheduleById.getDepname());
            orderInfo.setTitle(scheduleById.getTitle());
            orderInfo.setReserveDate(scheduleById.getReserveDate());
            orderInfo.setReserveTime(scheduleById.getReserveTime());
            orderInfo.setScheduleId(scheduleById.getHosScheduleId());
            orderInfo.setPatientId(patientById.getId());
            orderInfo.setPatientName(patientById.getName());
            orderInfo.setPatientPhone(patientById.getPhone());


            orderInfo.setHosRecordId(data.getString("hosRecordId"));
            orderInfo.setNumber(data.getInteger("number"));
            orderInfo.setFetchTime(data.getString("fetchTime"));
            orderInfo.setFetchAddress(data.getString("fetchAddress"));

            orderInfo.setAmount(scheduleById.getAmount());
            orderInfo.setQuitTime(scheduleById.getQuitTime());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            //3.2 如果返回能挂号，就把取医生排班信息、就诊人信息及第三方医院返回的信息都添加到order_info表中
            baseMapper.insert(orderInfo);



            //3.3 更新平台上对应医生的剩余可预约数
            //3.4 给就诊人发送短信提醒


            //4.返回订单的id
            return orderInfo.getId();

        }else{
            //3.1 如果返回不能挂号，直接抛出异常
            throw new YyghException(20001,"号源已满");
        }
    }


}
