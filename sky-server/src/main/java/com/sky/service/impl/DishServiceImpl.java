package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;

import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional  //事务注解 用户多表操作
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //菜品表插入一条数据
        dishMapper.insert(dish);

        //通过对insert的注解，获取其主键值
        Long dishId = dish.getId();
        //口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){   //判断用户有没有提交口味
            //遍历给dishId赋值
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
           dishFlavorMapper.insertBath(flavors);  //传入集合
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //TODO 后续更改为用redis进行存储，或者全用foreach进行处理，不用频繁的查询数据库
        //判断当前菜品是否能够删除--- 是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish= dishMapper.getById(id);  //为了复用性所以取全部数据
            //判断状态值是否等于1，即起售中
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除---是否被套餐关联了
        List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
        //判断是否被关联
        if(setmealIds != null && setmealIds.size() >0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.delectById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.delectByDishId(id);
        }

    }


    /**
     * 根据id查询并回显菜品
     * @param id
     */
    @Override
    public DishVO getById(Long id) {
        //根据菜品id查询菜品表的数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味表的数据
        List<DishFlavor> dishFlavor= dishMapper.getByDishId(id);

        //封装到VO对象
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);//这里用不了属性拷贝是因为dishFlavor是个封装的集合，而不是dishVO的 名为private List<DishFlavor> flavors的属性。虽然

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和口味信息
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品基本信息
        //因为DTO包含了口味数据所以
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //删除原有口味为数据
        dishFlavorMapper.delectByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){   //判断用户有没有提交口味
            //遍历给dishId赋值
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBath(flavors);  //传入集合
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getBycategoryId(Long categoryId) {
        log.info("分页id为："+categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<Dish> list = dishMapper.list(dish);
        log.info("123:{}",list);
        return list;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
