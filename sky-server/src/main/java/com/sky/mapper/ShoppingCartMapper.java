package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id修改菜品或套餐数量
     * @param shoppingCart
     */
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入菜品或者套餐数据
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);


    /**
     * 根据id删除数据
     * @param userId
     */
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
