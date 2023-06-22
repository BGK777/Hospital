package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 就诊人表(Patient)表服务实现类
 *
 * @author makejava
 * @since 2023-06-21 15:48:22
 */
@Service("patientService")
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Resource
    private DictFeignClient dictFeignClient;
    @Override
    public List<Patient> getPatentList(Long userId) {
        //根据userid获取旗下的就诊人列表
        LambdaQueryWrapper<Patient> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Patient::getUserId,userId);
        List<Patient> patientList = baseMapper.selectList(lqw);

        //遍历就诊人列表，封装每个就诊人里面的信息
        patientList.stream().forEach(item->{
            this.packPatient(item);
        });
        return patientList;
    }

    @Override
    public Patient detail(Long id) {
        Patient patient = baseMapper.selectById(id);
        packPatient(patient);
        return patient;
    }

    @Override
    public List<Patient> selectList(QueryWrapper<Patient> queryWrapper) {
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        patientList.stream().forEach(item->{
            packPatient(item);
        });
        return patientList;
    }

    private void packPatient(Patient patient){
        patient.getParam().put("certificatesTypeString",dictFeignClient.getName(patient.getCertificatesType()));
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        String cityString = dictFeignClient.getName(patient.getCityCode());
        String disctrictString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("provinceString",provinceString);
        patient.getParam().put("cityString",cityString);
        patient.getParam().put("districtString",disctrictString);

        patient.getParam().put("fullAddress",provinceString+cityString+disctrictString+patient.getAddress());
    }
}
