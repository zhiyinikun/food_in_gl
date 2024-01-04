package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//指定bean的名称，不然注入容器会和user的默认名称ShopController起冲突
@RestController("adminShopController")
@Slf4j
@Api(tags = "店铺")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    ShopService shopService;


    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态为：{}",status == 0 ?"已打烊":"营业中");
        //利用redis进行存值
        shopService.set(status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status=shopService.get();
        log.info("(商家)该店铺状态为：{}",status == 0 ?"已打烊":"营业中");
        return Result.success(status);
    }

}

