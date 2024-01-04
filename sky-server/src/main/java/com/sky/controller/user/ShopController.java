package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController ")
@Slf4j
@Api(tags = "店铺")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    /**
     * 获取店铺营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status=shopService.get();
        log.info("(用户)该店铺状态为：{}",status == 0 ?"已打烊":"营业中");
        return Result.success(status);
    }
//    TODO 商品减1的接口没有实现，后续加！ 请求路径是http://localhost:8080/user/shoppingCart/sub
}

