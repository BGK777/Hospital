package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 就诊人表(Patient)表服务接口
 * @author makejava
 * @since 2023-06-21 15:48:22
 */
public interface PatientService extends IService<Patient> {

    List<Patient> getPatentList(Long userId);

    Patient detail(Long id);

    List<Patient> selectList(QueryWrapper<Patient> queryWrapper);
}
