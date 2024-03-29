package com.gl.controller.user;

import com.gl.constant.StatusConstant;
import com.gl.entity.Dish;
import com.gl.result.Result;
import com.gl.service.DishService;
import com.gl.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品:{}",categoryId);
        //构造redis中的key，规则: dish 分类id
        String key = "dish_"+categoryId;
        List<Dish> list;
        //判断redis是否启动，未启动则直接查询数据库
        try{
        //查询redis 中是否存在菜品数据
         list = (List<Dish>) redisTemplate.opsForValue().get(key);

        if(list != null && list.size() > 0){
            //如果存在，直接返回，无须查询数据库
            return Result.success(list);
        }
        //如果不存在，查询数据库，将查询到的数据放入redis 中
        list =dishService.getBycategoryId(categoryId);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
        }catch (Exception e){
            list =dishService.getBycategoryId(categoryId);
            return Result.success(list);
        }
    }

}
