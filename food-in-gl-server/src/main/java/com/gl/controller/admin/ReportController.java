package com.gl.controller.admin;

import com.gl.result.Result;
import com.gl.service.ReportService;
import com.gl.vo.OrderReportVO;
import com.gl.vo.SalesTop10ReportVO;
import com.gl.vo.TurnoverReportVO;
import com.gl.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 数据统计接口
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,   //@DateTimeFormat指定格式
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计：{},{}", begin, end);
        TurnoverReportVO turnoverStatics = reportService.getTurnoverStatics(begin, end);
        return Result.success(turnoverStatics);
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,   //@DateTimeFormat指定格式
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计：{},{}", begin, end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
        return Result.success(userReportVO);
    }

    /**
     * 订单数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,   //@DateTimeFormat指定格式
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单数据统计: {},{}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }

    @GetMapping("/top10")
    @ApiOperation("销量top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,   //@DateTimeFormat指定格式
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量top10: {},{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }

}
