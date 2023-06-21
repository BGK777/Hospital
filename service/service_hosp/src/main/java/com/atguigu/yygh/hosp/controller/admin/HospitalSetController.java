package com.atguigu.yygh.hosp.controller.admin;


import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * 医院设置表(HospitalSet)表控制层
 *
 * @author makejava
 * @since 2023-04-22 14:49:36
 */
@RestController
@Api(tags = "医院设置信息")
@RequestMapping("admin/hosp/hospitalSet")
//@CrossOrigin //解决局部跨域问题
public class HospitalSetController{
    /**
     * 服务对象
     */
    @Resource
    private HospitalSetService hospitalSetService;

    /**
     * 医院信息的锁定与解锁
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "医院信息的锁定与解锁")
    @PostMapping("/updateStatus/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    /**
     * 批量删除
     * @param idList
     * @return
     */
    @ApiOperation(value = "批量删除")
    @DeleteMapping("/delete")
    public R batchDelete(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return R.ok();
    }

    /**
     * 根据id获取医院详细信息，也即回显
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取医院详细信息，也即回显")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id){
        return R.ok().data("item",hospitalSetService.getById(id));
    }

    /**
     * 修改医院信息
     * @param hospitalSet
     * @return
     */
    @ApiOperation(value = "修改医院信息")
    @PutMapping("/update")
    public R update(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    /**
     * 新增医院信息
     * @param hospitalSet
     * @return
     */
    @ApiOperation(value = "新增医院信息")
    @PostMapping("/save")
    public R save(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);

        //设置秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        //增加
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    /**
     * 分页查询
     * @param pageNum
     * @param size
     * @param HospSetVo
     * @return
     */
    @ApiOperation(value = "分页查询")
    @PostMapping("/page/{pageNum}/{size}")
    public R getPageInfo(@PathVariable Integer pageNum
                        , @PathVariable Integer size
                        , @RequestBody(required = false) HospitalSetQueryVo HospSetVo){
        Page<HospitalSet> page = hospitalSetService.page(pageNum,size,HospSetVo);

        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());

    }

    /**
     * 获取全部医院设置
     * @return
     */
    @ApiOperation(value = "获取全部医院设置")
    @GetMapping("/findAll")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list",list);
    }


    /**
     * 根据id删除医院信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除医院信息")
    @DeleteMapping("/deleteById/{id}")
    public R deleteById(@PathVariable("id") Integer id){
        if(hospitalSetService.removeById(id)) return R.ok();
        else return R.error();
    }
}

