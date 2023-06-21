package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.enums.StatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户表(UserInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-06-18 11:01:52
 */
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PatientService patientService;
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //验证非空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(20001,"输入不能为空!");
        }

        //验证验证码
        String redisCode = stringRedisTemplate.opsForValue().get(phone);
        if(!Objects.equals(code, redisCode)){
            throw new YyghException(20001,"验证码错误!");
        }

        String openid = loginVo.getOpenid();
        UserInfo userInfo = null;
        //判断openid是否为空，空代表执行的是手机号注册，非空则代表强制绑定手机号
        if(StringUtils.isEmpty(openid)){
            //验证是否为首次登录，如果是则注册用户信息
            LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserInfo::getPhone,phone);
            userInfo = baseMapper.selectOne(lqw);
            //不存在，注册
            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }else {
            //先查询找到拥有openid的记录
            LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserInfo::getOpenid,openid);
            userInfo = baseMapper.selectOne(lqw);

            //再查询拥有该手机号的用户
            LambdaQueryWrapper<UserInfo> phoneLqw = new LambdaQueryWrapper<>();
            phoneLqw.eq(UserInfo::getPhone,phone);
            UserInfo phoneUserInfo = baseMapper.selectOne(phoneLqw);

            //如果phoneUserInfo为空，则phone为其绑定手机号
            if(phoneUserInfo == null){
                userInfo.setPhone(phone);
                //更新
                baseMapper.updateById(userInfo);
            }else {
                phoneUserInfo.setOpenid(openid);
                phoneUserInfo.setNickName(userInfo.getNickName());
                //更新phoneUserInfo，并删除userInfo
                baseMapper.updateById(phoneUserInfo);
                baseMapper.deleteById(userInfo.getId());
            }
        }

        //验证用户状态
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户被锁定!");
        }

        //结果封装返回
        HashMap<String, Object> resMap = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        resMap.put("name", name);

        //根据用户名和用户id生成token，并加入返回结果中
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());
        resMap.put("token", token);

        return resMap;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        //将认证编号，转化为认证信息
        UserInfo userInfo = getById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public Page<UserInfo> getPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page=new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();

        lqw.like(!StringUtils.isEmpty(userInfoQueryVo.getKeyword()),UserInfo::getName,userInfoQueryVo.getKeyword())
                .like(!StringUtils.isEmpty(userInfoQueryVo.getKeyword()),UserInfo::getPhone,userInfoQueryVo.getKeyword())
                .eq(!StringUtils.isEmpty(userInfoQueryVo.getStatus()),UserInfo::getStatus,userInfoQueryVo.getStatus())
                .eq(!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus()),UserInfo::getAuthStatus,userInfoQueryVo.getAuthStatus())
                .gt(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeBegin()),UserInfo::getCreateTime,userInfoQueryVo.getCreateTimeBegin())
                .lt(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeEnd()),UserInfo::getCreateTime,userInfoQueryVo.getCreateTimeEnd());

        Page<UserInfo> page1 = baseMapper.selectPage(page, lqw);

        //遍历userinfo列表，把锁定状态编号和认证状态编号转化为文字信息
        page1.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });
        return page1;

    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if(status == 0 || status == 1){
            // UserInfo userInfo = baseMapper.selectById(id);
            //mp:支持直接修改的，
            UserInfo userInfo=new UserInfo();
            userInfo.setId(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 封装锁定状态编号和认证状态编号
     * @param item
     */
    private void packageUserInfo(UserInfo item) {
        Integer authStatus = item.getAuthStatus();
        Integer status = item.getStatus();
        item.getParam().put("statusString", StatusEnum.getStatusStringByStatus(status));
        item.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(authStatus));
    }

    @Override
    public Map<String, Object> detail(Long id) {
        UserInfo userInfo = baseMapper.selectById(id);

        QueryWrapper<Patient> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",id);
        List<Patient> patients =patientService.selectList(queryWrapper);

        Map<String, Object> map = new HashMap<String,Object>(2);
        map.put("userInfo",userInfo);
        map.put("patients",patients);
        return map;
    }
}
