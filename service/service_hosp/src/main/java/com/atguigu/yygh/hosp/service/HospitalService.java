package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> result);

    String getSignKeyWithHoscode(String requestHoscode);

    Hospital getHospitalByHoscode(String requestHoscode);

    Page<Hospital> getHospPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void lock(String id, Integer status);

    Hospital getHospById(String id);

    String getHospName(String hoscode);

    List<Hospital> getHospByName(String hosname);

    Hospital getHospByHoscode(String hoscode);
}
