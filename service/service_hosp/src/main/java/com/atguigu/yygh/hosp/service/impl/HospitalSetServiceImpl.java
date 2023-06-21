package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 医院设置表(HospitalSet)表服务实现类
 *
 * @author makejava
 * @since 2023-04-22 14:46:09
 */
@Service("hospitalSetService")
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Override
    public Page<HospitalSet> page(Integer pageNum, Integer size, HospitalSetQueryVo hospSetVo) {
        //分页构造器
        Page<HospitalSet> page = new Page<>(pageNum,size);

        LambdaQueryWrapper<HospitalSet> lqw = new LambdaQueryWrapper<>();

        //迷糊查询,在字段不为空的前提下查询
        lqw.eq(!StringUtils.isEmpty(hospSetVo.getHoscode()),HospitalSet::getHoscode,hospSetVo.getHoscode());
        lqw.like(!StringUtils.isEmpty(hospSetVo.getHosname()),HospitalSet::getHosname,hospSetVo.getHosname());

        page(page,lqw);

        return page;
    }
}
