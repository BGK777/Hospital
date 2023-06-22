package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * 排班持久层
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule findByHosScheduleId(String hosScheduleId);

    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date date);
}
