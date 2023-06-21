package com.atguigu.yygh.user.controller.user;

import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.WxApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
@RequestMapping("/user/userinfo/wx")
public class WxApiController {

    @Resource
    private WxApiService wxApiService;

    /**
     * 提供参数，前端生成二维码
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/param")
    @ResponseBody
    public R getConnect() throws UnsupportedEncodingException {
        Map<String,Object> resMap = wxApiService.getConnect();
        return R.ok().data(resMap);
    }

    /**
     * 提供给前端的回调函数
     * @param code
     * @param state
     * @return
     */
    @GetMapping("callback")
    public String callback(String code, String state) throws Exception {
        return wxApiService.callback(code,state);
    }

}
