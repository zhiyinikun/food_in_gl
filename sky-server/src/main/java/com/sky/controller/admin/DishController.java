package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation(value = "新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        //保存菜品和口味
        dishService.saveWithFlavor(dishDTO);

        //清理reidis的缓存数据
        String key = "dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping("")
    @ApiOperation(value = "批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品: {}",ids);
        dishService.deleteBatch(ids);
        //直接清理所有的菜品的reidis的缓存数据
        Set key = redisTemplate.keys("dish_*");
        //支持集合，所以不用去遍历
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询并回显菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询并回显菜品：{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping("")
    @ApiOperation(value = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //直接清理所有的reidis的缓存数据
        Set key = redisTemplate.keys("dish_*");
        //支持集合，所以不用去遍历
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        //构造redis中的key，规则: dish 分类id
        String key = "dish_"+categoryId;
        //查询redis 中是否存在菜品数据

        //如果存在，直接返四，无须查询数据库
        //如果不存在，查询数期库，将查询到的数据放入redis 中

        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> list =dishService.getBycategoryId(categoryId);
        return Result.success(list);
    }
    /** TODO
     * 这里修改旗手停售的状态的没写，为了到时候测试能测出来
     */
}
