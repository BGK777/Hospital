package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.result.R;
import com.atguigu.yygh.statistics.service.StatisticsService;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController()
@RequestMapping("/admin/statistic")
public class StatisticController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 预约统计
     * @param orderCountQueryVo
     * @return
     */
    @GetMapping("/countByDate")
    public R statistic(OrderCountQueryVo orderCountQueryVo){
        Map<String,Object> map = statisticsService.statistic(orderCountQueryVo);
        return R.ok().data(map);
    }
}
