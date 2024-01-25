package com.gl.mapper;

import com.gl.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    //批量插入口味数据
    void insertBath(List<DishFlavor> flavors);
    //批量删除口味数据
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void delectByDishId(Long dishId);
}
