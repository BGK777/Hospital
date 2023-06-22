package com.atguigu.yygh.msm.controller;

import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user/msm")
public class MsmController {
    @Resource
    private MsmService msmService;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/send/{phone}")
    public R send(@PathVariable("phone") String phone){
        boolean flag = msmService.send(phone);
        if(flag) return R.ok();
        else return R.error();
    }
}
