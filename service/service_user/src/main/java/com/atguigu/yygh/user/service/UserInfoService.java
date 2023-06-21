package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;


/**
 * 用户表(UserInfo)表服务接口
 *
 * @author makejava
 * @since 2023-06-18 11:01:52
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getUserInfo(Long userId);

    Page<UserInfo> getPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo);

    void updateStatus(Long id, Integer status);

    Map<String, Object> detail(Long id);
}
