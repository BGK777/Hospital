package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface MsmService {
    boolean send(String phone);

    void sendMessage(MsmVo msmVo);
}
