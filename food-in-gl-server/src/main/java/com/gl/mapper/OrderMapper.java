package com.gl.mapper;

import com.github.pagehelper.Page;
import com.gl.dto.GoodsSalesDTO;
import com.gl.dto.OrdersPageQueryDTO;
import com.gl.entity.OrderDetail;
import com.gl.entity.Orders;
import com.gl.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper

public interface OrderMapper {


    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     */

     @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
     void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    /**
     * 通过userId找orderid
     * @param userId
     * @return
     */
    @Select("select id from orders where user_id=#{userId} order by id desc limit 1")
    long getById(Long userId);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getByIdAll(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime time);


    /**
     * 根据日期查询总营业额
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Double sum(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 根据日期查询订单数据
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 销量排名前十
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop(LocalDateTime beginTime, LocalDateTime endTime);
}
