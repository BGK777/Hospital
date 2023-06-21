package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.prop.WeixinProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.service.WxApiService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxApiServiceImpl implements WxApiService {

    @Resource
    private WeixinProperties weixinProperties;

    @Resource
    private UserInfoService userInfoService;
    @Override
    public String callback(String code, String state) throws Exception {
        //拼接URL
        StringBuilder baseUrl = new StringBuilder()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String url = String.format(baseUrl.toString(),weixinProperties.getAppid()
                ,weixinProperties.getAppsecret()
                ,code);

         //利用工具类向微信平台发送get请求,返回JSON字符串
         String accessTokenInfo = HttpClientUtils.get(url);
         //转化为JSONObject,解析出token和openId
         JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
         String access_token = jsonObject.getString("access_token");
         String openid = jsonObject.getString("openid");

         //根据openidc查询，是否为第一次微信登录
         LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
         lqw.eq(UserInfo::getOpenid,openid);
         UserInfo userInfo = userInfoService.getOne(lqw);
         //判断用火是否存在，不存在则进行注册功能，存在则继续
         if(userInfo == null){
             //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
             //根据access_token和openid访问微信平台网址，获得用户信息
             StringBuilder baseUrll = new StringBuilder()
                     .append("https://api.weixin.qq.com/sns/userinfo")
                     .append("?access_token=%s")
                     .append("&openid=%s");
             String urll = String.format(baseUrll.toString(),access_token,openid);
             String uesrInfoStr = HttpClientUtils.get(urll);
             JSONObject jsonObject1 = JSONObject.parseObject(uesrInfoStr);

             //{"openid":"o3_SC5zTCowtRw_f2WXVqetPYP9M",
             // "nickname":"_",
             // "sex":0,
             // "language":"",
             // "city":"",
             // "province":"",
             // "country":"",
             // "headimgurl":"https:\/\/thirdwx.qlogo.cn\/mmopen\/vi_32\/pSeN0d6Wnof2lxKicASSKt6BOUVHGunczfpe07IkzcrSsW9NIGYWSYqGwalhfHUt45dDMibrhiayXcXKTicYV2AcRA\/132",
             // "privilege":[],
             // "unionid":"oWgGz1PKkD48tubWVwsIIyDvyy-g"}

             //解析出openid和nickname
             String backNickname = jsonObject1.getString("nickname");

             userInfo = new UserInfo();
             userInfo.setOpenid(openid);
             userInfo.setNickName(backNickname);
             userInfo.setStatus(1);
             userInfoService.save(userInfo);
         }

        //验证用户状态
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户被锁定!");
        }

        //结果封装返回
        HashMap<String, String> resMap = new HashMap<>();

        //检查这个用户手机号是否为空:为空，说明这是首次使用微信登录,强制绑定手机号
        if(StringUtils.isEmpty(userInfo.getPhone())){
            resMap.put("openid",openid);
        }else{//检查这个用户手机号是否为空:不为空，说明这不是首次微信登录
            resMap.put("openid","");
        }
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

        return "redirect:http://localhost:3000/weixin/callback?token="+resMap.get("token")+ "&openid="+resMap.get("openid")+"&name="+URLEncoder.encode(resMap.get("name"),"utf-8");
    }

    @Override
    public Map<String, Object> getConnect() throws UnsupportedEncodingException {
        Map<String,Object> resMap = new HashMap<>();
        String redirecturl = URLEncoder.encode(weixinProperties.getRedirecturl(), "UTF-8");
        resMap.put("appid",weixinProperties.getAppid());
        resMap.put("scope","snsapi_login");
        resMap.put("redirecturl",redirecturl);
        resMap.put("state",System.currentTimeMillis()+"");
        return resMap;
    }
}
