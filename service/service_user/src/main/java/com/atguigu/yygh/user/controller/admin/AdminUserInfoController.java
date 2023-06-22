package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/administrator/userinfo")
public class AdminUserInfoController {
    @Resource
    private UserInfoService userInfoService;

    /**
     * 分页带条件查询用户列表
     * @param pageNum
     * @param pageSize
     * @param userInfoQueryVo
     * @return
     */
    @GetMapping("/{pageNum}/{pageSize}")
    public R getUserinfoPage(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,
                             UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page = userInfoService.getPage(pageNum,pageSize,userInfoQueryVo);
        return R.ok().data("total",page.getTotal()).data("list",page.getRecords());
    }

    /**
     * 修改用户状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable("id") Long id,@PathVariable("status") Integer status){
        userInfoService.updateStatus(id,status);
        return R.ok();
    }

    /**
     * 根据id获取用户详细信息
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        Map<String, Object> resMap = userInfoService.detail(id);
        return R.ok().data(resMap);
    }

    /**
     * 用户认证实名
     * @param id
     * @param authStatus
     * @return
     */
    @PutMapping("/auth/{id}/{authStatus}")
    public R updateAuthStatus(@PathVariable("id") Long id,@PathVariable("authStatus") Integer authStatus){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAuthStatus(authStatus);
        userInfoService.updateById(userInfo);
        return R.ok();
    }
}
