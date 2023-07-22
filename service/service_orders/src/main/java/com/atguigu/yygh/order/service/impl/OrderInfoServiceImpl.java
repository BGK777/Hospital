package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.arbbitmq.MQEnums.MqConst;
import com.atguigu.yygh.arbbitmq.service.RabbitService;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.hosp.client.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.prop.WeixinPay;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.utils.HttpClient;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private RabbitService rabbitService;

    @Resource
    private WeiPayService weiPayService;

    @Resource
    private PaymentInfoService paymentService;

    @Override
    public Long submitOrder(String scheduleId, Long patientId) {
        //1.获取排班信息和，就诊人信息
        Patient patientById = patientFeignClient.getPatientById(patientId);
        ScheduleOrderVo scheduleById = hospitalFeignClient.getScheduleOrderById(scheduleId);


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
            //这个位置设置的是mangodb中的排盘_id,不能使用scheduleById.getHosScheduleId()
            orderInfo.setScheduleId(scheduleId);
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
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            orderMqVo.setAvailableNumber(data.getIntValue("availableNumber"));
            orderMqVo.setReservedNumber(data.getIntValue("reservedNumber"));

            MsmVo msmVo = new MsmVo();
            msmVo.setTemplateCode("您已经成功预约，不要迟到!");
            Map<String,Object> msmMap=new HashMap<>();
            msmMap.put("time",scheduleById.getReserveDate()+" "+scheduleById.getReserveTime());
            msmMap.put("name","杨永信");
            msmVo.setParam(msmMap);

            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);


            //4.返回订单的id
            return orderInfo.getId();

        }else{
            //3.1 如果返回不能挂号，直接抛出异常
            throw new YyghException(20001,"号源已满");
        }
    }

    @Override
    public Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {
        Page page=new Page(pageNum,pageSize);
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<OrderInfo>();

        Long userId = orderQueryVo.getUserId(); //用户id
        String outTradeNo = orderQueryVo.getOutTradeNo();//订单号
        String keyword = orderQueryVo.getKeyword();//医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人id
        String orderStatus = orderQueryVo.getOrderStatus();//订单状态
        String reserveDate = orderQueryVo.getReserveDate(); //预约日期
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();//下订单时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();//下订单时间

        if(!StringUtils.isEmpty(userId)){
            queryWrapper.eq("user_id", userId);
        }
        if(!StringUtils.isEmpty(outTradeNo)){
            queryWrapper.eq("out_trade_no", outTradeNo);
        }
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("hosname", keyword);
        }
        if(!StringUtils.isEmpty(patientId)){
            queryWrapper.eq("patient_id", patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            queryWrapper.eq("order_status", orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            queryWrapper.ge("reserve_date", reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time", createTimeEnd);
        }
        Page<OrderInfo> page1 = baseMapper.selectPage(page, queryWrapper);
        page1.getRecords().parallelStream().forEach(item->{
            this.packageOrderInfo(item);
        });

        return page1;
    }

    @Override
    public OrderInfo detail(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancel(Long orderId) {
        //取消预约时间与现在时间比较
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()){
            throw new YyghException(20001,"取消预约截止日期已过！");
        }

        //封装请求第三方医院平台的条件
        Map<String,Object>  hospitalParamMap=new HashMap<String,Object>();
        hospitalParamMap.put("hoscode",orderInfo.getHoscode());
        hospitalParamMap.put("hosRecordId",orderInfo.getHosRecordId());

        //再通知第三方医院是否可以退款
        JSONObject jsonObject = HttpRequestHelper.sendRequest(hospitalParamMap, "http://localhost:9998/order/updateCancelStatus");
        if(jsonObject == null || jsonObject.getIntValue("code") != 200){
            throw  new YyghException(20001,"取消失败");
        }
        //判断是否已付款，已付款则需要退款
        if(Objects.equals(orderInfo.getOrderStatus(), OrderStatusEnum.PAID.getStatus())){
            //已支付退款
            boolean flag = weiPayService.refund(orderId);
            if(!flag){
                throw new YyghException(20001,"退款失败");
            }
        }
        //更新订单表状态和支付记录表记录状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        UpdateWrapper<PaymentInfo> updateWrapper=new UpdateWrapper<PaymentInfo>();
        updateWrapper.eq("order_id",orderInfo.getId());
        updateWrapper.set("payment_status", PaymentStatusEnum.REFUND.getStatus());
        paymentService.update(updateWrapper);
        //回退主治医师的号源量
        OrderMqVo orderMqVo=new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        MsmVo msmVo=new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("您已取消预约");
        msmVo.setParam(null);
        orderMqVo.setMsmVo(msmVo);
        //6.给就诊人发送短信提示：
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);
    }

    private void packageOrderInfo(OrderInfo item) {
        item.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(item.getOrderStatus()));
    }

}
