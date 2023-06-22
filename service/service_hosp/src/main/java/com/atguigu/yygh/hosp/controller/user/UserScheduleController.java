package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Resource
    private ScheduleService scheduleService;

    /**
     * 科室排班预约按日期分页数据
     * @param hoscode
     * @param depcode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/{hoscode}/{depcode}/{pageNum}/{pageSize}")
    public R getSchedulePage(@PathVariable("hoscode") String hoscode,
                             @PathVariable("depcode") String depcode,
                             @PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize){
        Map<String,Object> resMap = scheduleService.getSchedulePage(hoscode,depcode,pageNum,pageSize);
        return R.ok().data(resMap);
    }

    /**
     * 查询workdate当天的排班列表
     * @param hoscode
     * @param depcode
     * @param workdate
     * @return
     */
    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R getScheduleDetail(@PathVariable("hoscode") String hoscode,
                               @PathVariable("depcode") String depcode,
                               @PathVariable("workdate") String workdate){
        List<Schedule> details = scheduleService.detail(hoscode, depcode, workdate);
        return R.ok().data("details",details);
    }

    /**
     * 根据id获取schedule
     * @param scheduleId
     * @return
     */
    @GetMapping("/info/{scheduleId}")
    public R getScheduleDetail(@PathVariable("scheduleId") String scheduleId){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return R.ok().data("schedule",schedule);
    }
}
