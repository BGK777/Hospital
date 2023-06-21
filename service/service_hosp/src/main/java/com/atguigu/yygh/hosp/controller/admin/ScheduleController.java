package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    /**
     * //根据医院编号 和 科室编号 ，查询排班规则数据
     * @param pageNum
     * @param pageSize
     * @param hoscode
     * @param depcode
     * @return
     */
    @ApiOperation(value ="查询排班规则数据")
    @GetMapping("/getScheduleRule/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getScheduleRule(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,
                             @PathVariable("hoscode") String hoscode,
                             @PathVariable("depcode") String depcode){
        Map<String,Object> resMap = scheduleService.getScheduleRule(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(resMap);
    }

    /**
     * 查询排班详细信息数据
     * @param hoscode
     * @param depcode
     * @param workdate
     * @return
     */
    @ApiOperation(value ="查询排班详细信息数据")
    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R detail( @PathVariable String hoscode,
                     @PathVariable String depcode,
                     @PathVariable String workdate){

        List<Schedule> scheduleList= scheduleService.detail(hoscode,depcode,workdate);
        return R.ok().data("list",scheduleList);
    }
}
