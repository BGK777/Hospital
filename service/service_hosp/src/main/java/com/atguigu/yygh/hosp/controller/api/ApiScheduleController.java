package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.MapHelper;
import com.atguigu.yygh.hosp.utils.SignKeyHelper;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 排班控制器
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {
    @Resource
    private ScheduleService scheduleService;

    @Resource
    private HospitalService hospitalService;

    /**
     * 排班添加
     * @param request
     * @return
     */
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> resultMap= MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String)resultMap.get("sign");
        String requestHoscode = (String)resultMap.get("hoscode");
        String platformSignKey= hospitalService.getSignKeyWithHoscode(requestHoscode);
        //signkey验证
        if(SignKeyHelper.Helper(requestSignKey,platformSignKey)){
            scheduleService.saveSchedule(resultMap);
            return Result.ok();
        }else{
            throw  new YyghException(20002,"签名验证失败！");
        }
    }


    /**
     * 排班分页查询
     * @param request
     * @return
     */
    @PostMapping("/schedule/list")
    public Result getPageDepartment(HttpServletRequest request) {
        Map<String, Object> resultMap = MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String) resultMap.get("sign");
        String requestHoscode = (String) resultMap.get("hoscode");
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHoscode);

        //signkey验证
        if (SignKeyHelper.Helper(requestSignKey, platformSignKey)) {
            Page<Schedule> page = scheduleService.getPage(resultMap);
            return Result.ok(page);
        } else {
            throw new YyghException(20002, "签名验证失败！");
        }
    }

    /**
     * 删除排班信息
     * @param request
     * @return
     */
    @PostMapping("/schedule/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, Object> resultMap = MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String) resultMap.get("sign");
        String requestHoscode = (String) resultMap.get("hoscode");
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHoscode);

        //signkey验证
        if (SignKeyHelper.Helper(requestSignKey, platformSignKey)) {
            scheduleService.removeSchedule(resultMap);
            return Result.ok();
        }   else {
            throw new YyghException(20002, "签名验证失败！");
        }
    }
}
