package com.gl.service.impl;


import com.gl.dto.GoodsSalesDTO;
import com.gl.entity.Orders;
import com.gl.mapper.OrderMapper;
import com.gl.mapper.UserMapper;
import com.gl.service.ReportService;
import com.gl.vo.OrderReportVO;
import com.gl.vo.SalesTop10ReportVO;
import com.gl.vo.TurnoverReportVO;
import com.gl.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 指定时间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatics(LocalDate begin, LocalDate end) {
        log.info("天数为:{},{}",begin,end);
        //用于存放天数
        ArrayList<LocalDate> dataList = new ArrayList<>();
        //存放营业额
        ArrayList<Double> turnoverList = new ArrayList<>();


        while (!begin.equals(end.plusDays(1))){

            //日期扩充到秒
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//一天的开始时刻进行日期转换
            LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);//一天的结束时刻
            Double turnover=orderMapper.sum(beginTime,endTime,Orders.CANCELLED);
            turnover = turnover == null ? 0.0 :turnover;

            //将指定包括第一天后以及包括最后一天前的日期都放入数组中
            turnoverList.add(turnover);
            dataList.add(begin);

            //日期加一
            begin = begin.plusDays(1);
        }
        //利用工具类将数组拼接成以逗号为分隔的字符串
        String data = StringUtils.join(dataList, ",");
        String turnover = StringUtils.join(turnoverList, ",");


        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(data)
                .turnoverList(turnover)
                .build();




        return turnoverReportVO;
    }


    /**
     * 指定时间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        log.info("天数为:{},{}",begin,end);
        //用于存放天数
        ArrayList<LocalDate> dataList = new ArrayList<>();
        //用于存放每天用户总量
        ArrayList<Integer> totalUserList = new ArrayList<>();
        //用于存放每天新增用户
        ArrayList<Integer> newUserList = new ArrayList<>();

        while (!begin.equals(end.plusDays(1))){

            //日期扩充到秒
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//一天的开始时刻进行日期转换
            LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);//一天的结束时刻

            Map map = new HashMap();
            map.put("endTime",endTime);
            //查询用户总量
            Integer totalUser=userMapper.count(map);

            map.put("beginTime", beginTime);
            //查询每天新增用户数量
            Integer newUser=userMapper.count(map);


            //将指定包括第一天后以及包括最后一天前的日期都放入数组中
            dataList.add(begin);
            //将指定包括最后一天前的用户都放入数组中
            totalUserList.add(totalUser);
            //将指定包括第一天后以及包括最后一天前的用户都放入数组中
            newUserList.add(newUser);


            //日期加一
            begin = begin.plusDays(1);
        }

        //利用工具类将数组拼接成以逗号为分隔的字符串
        String data = StringUtils.join(dataList, ",");
        String totalUser = StringUtils.join(totalUserList, ",");
        String newUser = StringUtils.join(newUserList, ",");

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(data)
                .totalUserList(totalUser)
                .newUserList(newUser)
                .build();
        return userReportVO;
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        //存放每天订单数
        List<Integer> orderCountList = new ArrayList();
        //存放每天有效订单数
        List<Integer> validOrderCountList = new ArrayList();


        //遍历dateList集合，查询每天的有效订单数和订单总数
        for(LocalDate date : dateList) {
            // 查询每天的订单总数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//一天的开始时刻进行日期转换
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//一天的结束时刻

            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);

        }

        //计算时间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();//意思是把orderCountList遍历然后sum求和，然后 get() 获取值

        //计算时间内的有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }//强转

        return OrderReportVO.builder()
                        .dateList(StringUtils.join(dateList,  ","))
                        .orderCountList(StringUtils.join(orderCountList, ","))
                        .validOrderCountList(StringUtils.join(validOrderCountList,  ","))
                        .totalOrderCount(totalOrderCount)
                        .validOrderCount(validOrderCount)
                        .orderCompletionRate(orderCompletionRate)
                        .build();
    }

    /**根据条件统计订单数量

    * @param status
    * @return
    */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    /**
     * 指定时间内的销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//开始时刻进行日期转换
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);//结束时刻
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop(beginTime,endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names,  ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO ::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers,  ",");

        //返回结果
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
