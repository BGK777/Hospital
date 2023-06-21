package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.utils.MapHelper;
import com.atguigu.yygh.hosp.utils.SignKeyHelper;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 医院信息控制器
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Resource
    private HospitalService hospitalService;

    /**
     * 添加科室信息
     * @param request
     * @return
     */
    @PostMapping("/saveHospital")
    public Result<Hospital> saveHospital(HttpServletRequest request){
        //1.获取所有的参数
        Map<String, Object> resultMap= MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String)resultMap.get("sign");
        String requestHoscode = (String)resultMap.get("hoscode");
        String platformSignKey= hospitalService.getSignKeyWithHoscode(requestHoscode);
        //signkey验证
        if(SignKeyHelper.Helper(requestSignKey,platformSignKey)){
            String logoData = (String)resultMap.get("logoData");
            String result = logoData.replaceAll(" ", "+");
            resultMap.put("logoData",result);
            hospitalService.saveHospital(resultMap);
            return Result.ok();
        }else{
            throw  new YyghException(20002,"签名验证失败！");
        }
    }

    /**
     * 查询医院信息
     * @param request
     * @return
     */
    @PostMapping("/hospital/show")
    public Result<Hospital> show(HttpServletRequest request){
        //1.获取所有的参数
        Map<String, Object> resultMap= MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String)resultMap.get("sign");
        String requestHoscode = (String)resultMap.get("hoscode");
        String platformSignKey= hospitalService.getSignKeyWithHoscode(requestHoscode);
        //signkey验证
        if(SignKeyHelper.Helper(requestSignKey,platformSignKey)){
            Hospital hospital=hospitalService.getHospitalByHoscode(requestHoscode);
            return Result.ok(hospital);
        }else{
            throw  new YyghException(20002,"签名验证失败！");
        }
    }
}
