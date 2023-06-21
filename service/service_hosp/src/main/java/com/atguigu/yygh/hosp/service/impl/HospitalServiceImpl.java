package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class HospitalServiceImpl implements HospitalService {
    @Resource
    private HospitalRepository hospitalRepository;

    @Resource
    private HospitalSetMapper hospitalSetMapper;

    //注入远程调用客户端
    @Resource
    private DictFeignClient dictFeignClient;

    @Override
    public void saveHospital(Map<String, Object> result) {
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(result), Hospital.class);
        String hoscode = hospital.getHoscode();
        Hospital collection = hospitalRepository.findByHoscode(hoscode);

        if(collection == null){//平台上没有该医院信息做添加
            //0
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{//平台上有该医院信息做修改
            hospital.setStatus(collection.getStatus());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(collection.getIsDeleted());
            hospital.setId(collection.getId());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public String getSignKeyWithHoscode(String requestHoscode) {
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper=new QueryWrapper<HospitalSet>();
        hospitalSetQueryWrapper.eq("hoscode", requestHoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(hospitalSetQueryWrapper);
        if(hospitalSet == null){
            throw new YyghException(20001,"该医院信息不存在");
        }
        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getHospitalByHoscode(String requestHoscode) {
        return hospitalRepository.findByHoscode(requestHoscode);
    }

    @Override
    public Page<Hospital> getHospPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        //排序规则
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //分页构造器
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);

        //条件对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        //匹配器，如何使用查询条件，支持模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                        .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains())
                        .withIgnoreCase(true);

        Example<Hospital> example = Example.of(hospital,exampleMatcher);

        Page<Hospital> hospitalPage = hospitalRepository.findAll(example, pageable);

        //使用stream流，把数据中的编号转化为文字
        hospitalPage.getContent().stream().forEach(this::packHospital);
        return hospitalPage;
    }

    @Override
    public void lock(String id, Integer status) {
        if(status == 0 || status == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        //使用stream流，把数据中的编号转化为文字
        packHospital(hospital);
        return hospital;
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    @Override
    public List<Hospital> getHospByName(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Hospital getHospByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        packHospital(hospital);
        return hospital;
    }

    /**
     * 封装医院信息
     * @param item
     */
    private void packHospital(Hospital item) {
        //获取医院等级编码
        String hostype = item.getHostype();

        //获取省市区的编码
        String provinceCode = item.getProvinceCode();
        String cityCode = item.getCityCode();
        String districtCode = item.getDistrictCode();

        //通过Dict远程客户端调用
        //省市区名称
        String provinceName = dictFeignClient.getName(provinceCode);
        String cityName = dictFeignClient.getName(cityCode);
        String districtName = dictFeignClient.getName(districtCode);
        //医院等级
        String level = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hostype);

        item.getParam().put("level",level);
        item.getParam().put("address",provinceName + cityName + districtName + item.getAddress());
    }
}
