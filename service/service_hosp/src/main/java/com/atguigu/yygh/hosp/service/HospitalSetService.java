package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 医院设置表(HospitalSet)表服务接口
 *
 * @author makejava
 * @since 2023-04-22 14:46:09
 */
public interface HospitalSetService extends IService<HospitalSet> {

    Page<HospitalSet> page(Integer pageNum, Integer size, HospitalSetQueryVo hospSetVo);
}
