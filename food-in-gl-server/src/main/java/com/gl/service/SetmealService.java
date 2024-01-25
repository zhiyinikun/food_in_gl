package com.gl.service;

import com.gl.dto.DishPageQueryDTO;
import com.gl.dto.SetmealDTO;
import com.gl.entity.Setmeal;
import com.gl.result.PageResult;
import com.gl.vo.DishItemVO;
import com.gl.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

     PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void saveWithSetmeal(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO getByIdWithDish(Long id);

    void update(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

}
