package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> resultMap);

    Page<Schedule> getPage(Map<String, Object> resultMap);

    void removeSchedule(Map<String, Object> resultMap);

    Map<String, Object> getScheduleRule(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> getSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize);

    Schedule getScheduleById(String scheduleId);

    ScheduleOrderVo getScheduleOrderById(String scheduleId);
}
