package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> resultMap) {
        //map转化为Dempartment对象
        Department department = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Department.class);
        //查询mongodb中是否存在
        Department isDepartment = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //不存在，添加操作
        if(isDepartment == null){
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setCreateTime(isDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(isDepartment.getIsDeleted());
            department.setId(isDepartment.getId());
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> getPage(Map<String, Object> resultMap) {
        int page = Integer.parseInt((String) resultMap.get("page"));
        int limit = Integer.parseInt((String) resultMap.get("limit"));
        String hoscode = (String) resultMap.get("hoscode");

        Pageable pageable = PageRequest.of(page-1,limit);
        Department department = new Department();
        department.setHoscode(hoscode);

        Example<Department> example = Example.of(department);
        return departmentRepository.findAll(example,pageable);
    }

    @Override
    public void removeDepartment(Map<String, Object> resultMap) {
        String hoscode = (String) resultMap.get("hoscode");
        String depcode = (String) resultMap.get("depcode");

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);

        if (department != null){
            departmentRepository.deleteById(department.getId());
        }else {
            throw new YyghException(20003,"该医院不存在！，无法删除");
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //结果返回集合
        List<DepartmentVo> resList = new ArrayList<>();

        //先查询指定医院code下的所有排班
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        List<Department> departmentList = departmentRepository.findAll(example);
        //根据bigCode进行stream流分组
        Map<String, List<Department>> departmentMap = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getBigcode));

        //遍历大科室，并封装
        for(Map.Entry<String, List<Department>> entry : departmentMap.entrySet()){
            //大科室编号
            String bigCode = entry.getKey();
            //大科室对应的全部科室
            List<Department> departmentList1 = entry.getValue();
            //构建封装载体
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentList1.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> childs = new ArrayList<>();
            for(Department department : departmentList1){
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(department.getDepcode());
                departmentVo1.setDepname(department.getDepname());
                childs.add(departmentVo1);
            }
            departmentVo.setChildren(childs);

            //添加到结果返回集合
            resList.add(departmentVo);
        }
        return resList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        return department.getDepname();
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);
    }
}
