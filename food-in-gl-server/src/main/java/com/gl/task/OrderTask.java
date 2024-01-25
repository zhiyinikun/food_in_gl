package com.gl.task;

import com.gl.entity.Orders;
import com.gl.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 订单超时30分钟更新支付状态为已取消
     */
    @Scheduled(cron = "0 0/10 * * * ?  ") //corn表达式，表示每分钟触发一次
    public void timeOutOrder(){
        log.info("处理超时订单：{}", LocalDateTime.now());
        //当前时间减去30十分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-30);
        Integer status = Orders.PENDING_PAYMENT;
        List<Orders> OrderList = orderMapper.getByStatusAndOrderTime(status,time);
        if(OrderList != null && OrderList.size()>0){
            for (Orders orders : OrderList) {
                //更新为已取消
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("取消原因：订单已超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
    /**
     * 由于商家未确认，一直处于派送中。凌晨三点更新
     */
    @Scheduled(cron = "0 0 3 * * ? ")
    public void DeliveryOrder(){
        log.info("处理长时间派送中的订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusHours(-3);
        List<Orders> OrderList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT,time);
        if(OrderList != null && OrderList.size()>0){
            for (Orders orders : OrderList) {
                //更新为已取消
                orders.setStatus(Orders.CANCELLED);
                orderMapper.update(orders);
            }
        }
    }
}
