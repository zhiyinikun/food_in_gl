package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 判断SetmealDish的dish_id是否关联了id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 插入与套餐关联的菜品
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);

    List<SetmealDish> getBySetmealId(Long id);
}
