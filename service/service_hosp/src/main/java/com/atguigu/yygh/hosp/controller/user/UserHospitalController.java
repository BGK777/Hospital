package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Resource
    private HospitalService hospitalService;

    /**
     * 用户获取医院列表，不分页
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "获取医院列表")
    @GetMapping("/list")
    public R getHospList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospPage = hospitalService.getHospPage(1, 1000, hospitalQueryVo);
        return R.ok().data("list",hospPage.getContent());
    }

    /**
     * 用户根据医院名称迷糊查询医院列表
     * @param hosname
     * @return
     */
    @GetMapping("/{hosname}")
    @ApiOperation(value = "获取医院列表")
    public R getHospByHosName(@PathVariable("hosname") String hosname){
        List<Hospital> hospitals = hospitalService.getHospByName(hosname);
        return R.ok().data("list",hospitals);
    }

    /**
     * 获取医院详情
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "获取医院详情")
    @GetMapping("/detail/{hoscode}")
    public R getHospByHoscode(@PathVariable("hoscode") String hoscode){
        Hospital hospital = hospitalService.getHospByHoscode(hoscode);
        return R.ok().data("hospital",hospital);
    }
}
