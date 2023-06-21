package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.utils.MapHelper;
import com.atguigu.yygh.hosp.utils.SignKeyHelper;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 科室控制器
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {
    @Resource
    private HospitalService hospitalService;
    @Resource
    private DepartmentService departmentService;

    /**
     * 添加科室
     * @param request
     * @return
     */
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, Object> resultMap= MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String)resultMap.get("sign");
        String requestHoscode = (String)resultMap.get("hoscode");
        String platformSignKey= hospitalService.getSignKeyWithHoscode(requestHoscode);
        //signkey验证
        if(SignKeyHelper.Helper(requestSignKey,platformSignKey)){
            departmentService.saveDepartment(resultMap);
            return Result.ok();
        }else{
            throw  new YyghException(20002,"签名验证失败！");
        }
    }

    /**
     * 科室分页查询
     * @param request
     * @return
     */
    @PostMapping("/department/list")
    public Result getPageDepartment(HttpServletRequest request) {
        Map<String, Object> resultMap = MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String) resultMap.get("sign");
        String requestHoscode = (String) resultMap.get("hoscode");
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHoscode);

        //signkey验证
        if (SignKeyHelper.Helper(requestSignKey, platformSignKey)) {
            Page<Department> page = departmentService.getPage(resultMap);
            return Result.ok(page);
        } else {
            throw new YyghException(20002, "签名验证失败！");
        }
    }

    /**
     * 删除科室信息
     * @param request
     * @return
     */
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, Object> resultMap = MapHelper.Helper(request.getParameterMap());
        String requestSignKey = (String) resultMap.get("sign");
        String requestHoscode = (String) resultMap.get("hoscode");
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHoscode);

        //signkey验证
        if (SignKeyHelper.Helper(requestSignKey, platformSignKey)) {
            departmentService.removeDepartment(resultMap);
            return Result.ok();
        }   else {
            throw new YyghException(20002, "签名验证失败！");
        }
    }
}
