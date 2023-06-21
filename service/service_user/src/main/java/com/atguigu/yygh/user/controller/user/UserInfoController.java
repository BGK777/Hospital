package com.atguigu.yygh.user.controller.user;

import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户模块控制层
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        Map<String,Object> resMap = userInfoService.login(loginVo);
        return R.ok().data(resMap);
    }

    /**
     * 获取用户id信息接口
     * @param token
     * @return
     */
    @ApiOperation(value = "获取用户id信息接口")
    @GetMapping("/info")
    public R getUserInfo(@RequestHeader String token ){
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return R.ok().data("user",userInfo);
    }

    /**
     * 认证数据保存到数据库
     * @param token
     * @param userAuthVo
     * @return
     */
    @ApiOperation(value = "认证数据保存到数据库")
    @PutMapping("/update")
    public R update(@RequestHeader String token, UserAuthVo userAuthVo){
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo=new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        userInfoService.updateById(userInfo);

        return R.ok();
    }
}
