package com.gl.service;

import com.gl.vo.OrderReportVO;
import com.gl.vo.SalesTop10ReportVO;
import com.gl.vo.TurnoverReportVO;
import com.gl.vo.UserReportVO;


import java.time.LocalDate;


public interface ReportService {
    /**
     * 指定时间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatics(LocalDate begin,LocalDate end);

    /**
     * 指定时间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 指定时间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 指定时间内的销量排名
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
