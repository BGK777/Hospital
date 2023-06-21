package com.atguigu.yygh.user.controller.user;


import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.api.ApiController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 就诊人表(Patient)表控制层
 *
 * @author makejava
 * @since 2023-06-21 15:48:22
 */
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private PatientService patientService;

    /**
     * 查询所有数据
     * @param token
     * @return 所有数据
     */
    @GetMapping("/all")
    public R selectAll(@RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        List<Patient> patientList = patientService.getPatentList(userId);
        return R.ok().data("list",patientList);
    }

    /**
     * 增加就诊人
     * @param patient
     * @param token
     * @return
     */
    @PostMapping("/save")
    public R save(@RequestBody Patient patient,@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    /**
     * 删除就诊人
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id){
        patientService.removeById(id);
        return R.ok();
    }

    /**
     * 回显数据
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Patient patient = patientService.detail(id);
        return R.ok().data("patient",patient);
    }

    /**
     * 修改信息
     * @param patient
     * @return
     */
    @PutMapping("/update")
    public R update(@RequestBody Patient patient){
        patientService.updateById(patient);
        return R.ok();
    }
}

