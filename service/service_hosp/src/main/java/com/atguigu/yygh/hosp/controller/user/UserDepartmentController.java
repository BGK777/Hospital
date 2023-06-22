package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/hosp/department")
public class UserDepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 获取医院的科室信息
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "获取医院的科室信息")
    @GetMapping("/all/{hoscode}")
    public R getDepartment(@PathVariable("hoscode") String hoscode){
        List<DepartmentVo> departmentList = departmentService.findDeptTree(hoscode);
        return R.ok().data("list",departmentList);
    }
}
