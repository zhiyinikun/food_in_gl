package com.gl.mapper;

import com.github.pagehelper.Page;
import com.gl.annotation.AutoFill;
import com.gl.dto.DishPageQueryDTO;
import com.gl.entity.Dish;
import com.gl.entity.DishFlavor;
import com.gl.enumeration.OperationType;
import com.gl.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {



    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据菜品id查询菜品数据
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from dish where id =#{id}")
    void delectById(Long id);

    /**
     * 根据菜品id查询口味表的数据
     * @param
     * @return
     */
    @Select("select * from dish_flavor where dish_id =#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据id修改菜品基本信息
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */

    List<Dish> list(Dish dish);

    List<Dish> getBySetmealId(Long id);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
