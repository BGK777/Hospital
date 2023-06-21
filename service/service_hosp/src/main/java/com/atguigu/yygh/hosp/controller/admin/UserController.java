package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.model.acl.User;
import com.atguigu.yygh.result.R;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/admin/user")
//@CrossOrigin //解决局部跨域问题
public class UserController {


    @PostMapping("/login")
    public R login(@RequestBody User user){
     //暂时不去数据库查:用户系统再去
         return R.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public R info(String token){
        return R.ok().data("roles","[admin]")
                     .data("introduction","I am a super administrator")
                     .data("avatar","https://ts1.cn.mm.bing.net/th/id/R-C.61f9f24630b1a536c4e1f02d94de63bb?rik=bHf8BLinRkXmCg&riu=http%3a%2f%2fwww.cosplay8.com%2fuploads%2fallimg%2f121023%2f15305V635-1.gif&ehk=j2%2bPOndJFDPb8mF28f9HvHsWHHQ%2fE68p0xbuK4hyUBg%3d&risl=&pid=ImgRaw&r=0")
                     .data("name","Super Admin");
    }


}