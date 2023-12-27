package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

     PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void saveWithSetmeal(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO getByIdWithDish(Long id);

    void update(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);
}
