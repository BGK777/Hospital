package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/hosp")
//@CrossOrigin //解决局部跨域问题
public class HospitalController {

    /**
     * 与api第三方控制层共用一个service
     */
    @Resource
    private HospitalService hospitalService;

    @ApiOperation(value = "分页条件查询医院信息")
    @GetMapping("/{pageNum}/{pageSize}")
    public R getHospPage(@PathVariable("pageNum") Integer pageNum,
                                                 @PathVariable("pageSize") Integer pageSize,
                                                 HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pages = hospitalService.getHospPage(pageNum,pageSize,hospitalQueryVo);
        return R.ok().data("total",pages.getTotalElements()).data("list",pages.getContent());
    }

    /**
     * 更改医院状态，上线，或下线
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "更新上线状态")
    @PutMapping("updateStatus/{id}/{status}")
    public R lock(@PathVariable("id") String id,@PathVariable("status") Integer status){
        hospitalService.lock(id,status);
        return R.ok();
    }

    /**
     * 获取医院详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "获取医院详细信息")
    @GetMapping("detail/{id}")
    public R hospDetailById(@PathVariable("id") String id){
        Hospital hospital = hospitalService.getHospById(id);
        return R.ok().data("hospital",hospital);
    }

}
