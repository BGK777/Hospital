package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Resource
    private ScheduleRepository scheduleRepository;

    @Resource
    private HospitalService hospitalService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private DepartmentService departmentService;

    @Override
    public void saveSchedule(Map<String, Object> resultMap) {
        //map转为schedule对象
        Schedule schedule = JSONObject.parseObject(JSON.toJSONString(resultMap), Schedule.class);

        Schedule isSchedule = scheduleRepository.findByHosScheduleId(schedule.getHosScheduleId());

        if(isSchedule == null){
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(isSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setId(isSchedule.getId());
            schedule.setIsDeleted(isSchedule.getIsDeleted());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getPage(Map<String, Object> resultMap) {
        int page = Integer.parseInt((String) resultMap.get("page"));
        int limit = Integer.parseInt((String) resultMap.get("limit"));
        String hoscode = (String) resultMap.get("hoscode");

        Pageable pageable = PageRequest.of(page-1,limit);
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);

        Example<Schedule> example = Example.of(schedule);
        return scheduleRepository.findAll(example,pageable);
    }

    @Override
    public void removeSchedule(Map<String, Object> resultMap) {
        String hoscode = (String) resultMap.get("hoscode");
        String hosScheduleId = (String) resultMap.get("hosScheduleId");

        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }else {
            throw new YyghException(20003,"该医院不存在！，无法删除");
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);



        //聚合:最好使用mongoTemplate
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)

        );
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        //当前页对应的列表数据
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            //工具类：美年旅游：周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate"));
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        Map<String, Object> map=new HashMap<String,Object>();
        map.put("list",mappedResults);
        map.put("total",aggregate2.getMappedResults().size());

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());

        map.put("baseMap",baseMap);

        return map;
    }

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        Date date = new DateTime(workdate).toDate();
        List<Schedule> scheduleList =scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);

        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    @Override
    public Map<String, Object> getSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize) {
        //获取当前医院的预约规则
        Hospital hospital = hospitalService.getHospByHoscode(hoscode);
        if(hospital == null){
            throw new YyghException(20001,"该医院信息不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获得预约周期内日期分页数据
        IPage<Date> page = getDateList(pageNum,pageSize,bookingRule);
        //获取当前页的日期列表
        List<Date> records = page.getRecords();

        //插叙条件构造
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(records);

        //聚合构造
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                        //分组条件
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        //总预约数
                        .sum("reservedNumber").as("reservedNumber")
                        //总剩余预约数
                        .sum("availableNumber").as("availableNumber"),
                //排序规则
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );

        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        //获得每日排班预约数据，并转化为Map
        List<BookingScheduleRuleVo> BookingScheduleRuleVoList = aggregate.getMappedResults();
        //以当天日期为key,排班数据value封装为Map
        Map<Date, BookingScheduleRuleVo> collect = BookingScheduleRuleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                BookingScheduleRuleVo -> BookingScheduleRuleVo));

        //构建一个List，用以返回当前页的所有排班数据
        ArrayList<BookingScheduleRuleVo> bookingScheduleRuleVos = new ArrayList<>();
        int size = records.size();

        //遍历当前分页的日期，再从collect，根据日期作为key取相应的当天排班数据并封装到bookingScheduleRuleVos
        for (int i = 0; i < size; i++) {
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);

            //取出的bookingScheduleRuleVo可能为null，因为预约周期里不一定会有号
            if(bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setReservedNumber(0);
                bookingScheduleRuleVo.setAvailableNumber(0);//当天所有医生的总的剩余可预约数
            }

            //共同设置的属性
            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(getDayOfWeek(new DateTime(date)));
            bookingScheduleRuleVo.setStatus(0);

            //需要对第一天和最后一天的状态做特殊处理
            //第一天
            if(pageNum == 1 && i == 0){
                //如果当前时间已经超过当天可预约的结束时间，则设置为当天已停止挂号
                DateTime dateTime = getDateTime(new Date(), bookingRule.getStopTime());
                if(dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            //最后一天
            if(pageNum == page.getPages() && i == (size-1)){
                bookingScheduleRuleVo.setStatus(1);
            }

            //做完处理，可以放入list
            bookingScheduleRuleVos.add(bookingScheduleRuleVo);
        }

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("total",page.getTotal());
        map.put("list",bookingScheduleRuleVos);

        Map<String,Object> baseMap = new HashMap<String,Object>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospitalByHoscode(hoscode).getHosname());
        //科室
        Department department=departmentService.getDepartment(hoscode,depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());

        map.put("baseMap",baseMap);

        return map;

    }

    @Override
    public Schedule getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        ScheduleOrderVo scheduleOrderVo=new ScheduleOrderVo();
        //基础信息复制到Vo
        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());

        //获取科室名称和安排日期时间
        Department department = departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());


        //获取预约退好截止时间
        DateTime dateTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(hospital.getBookingRule().getQuitDay()).toDate(), hospital.getBookingRule().getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toDate()); //预约的退号截止时间


        //设置当天预约截止时间
        scheduleOrderVo.setStopTime(getDateTime(schedule.getWorkDate(), hospital.getBookingRule().getStopTime()).toDate());

        return scheduleOrderVo;
    }

    private IPage<Date> getDateList(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        //获取预约周期
        Integer cycle = bookingRule.getCycle();
        //接下来判断当前时间是否已经过了医院的当天预约开启时间，如果过了，则cycle = cycle + 1
        String releaseTime = bookingRule.getReleaseTime();
        //获得当前的DateTime格式的时间
        DateTime dateTime = getDateTime(new Date(), releaseTime);
        //判断
        if(dateTime.isBeforeNow()){
            //已过起始时间，加一
            cycle+=1;
        }

        ArrayList<Date> list = new ArrayList<>();
        //把预约周期内的时间数据放入List中
        for (int i = 0; i < cycle; i++) {
            list.add(new DateTime(new DateTime().plusDays(i).toString("yyyy-MM-dd")).toDate());
        }

        ArrayList<Date> currentList = new ArrayList<>();
        //分页操作，计算出当前页起始日期，结束日期,以此从list获取数据放入currentList
        int start = (pageNum - 1) * pageSize;
        int end = (list.size() - start < pageSize) ? list.size() : (start + pageSize);
        for (int i = start; i < end; i++) {
            currentList.add(list.get(i));
        }

        //构建MP分页page
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date> page
                = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum,pageSize,list.size());
        page.setRecords(currentList);

        return page;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    /**
     * 包装排班信息
     * @param schedule
     */
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}


