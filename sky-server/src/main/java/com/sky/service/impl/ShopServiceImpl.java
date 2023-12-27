package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 设置店铺的营业状态
     * @param status
     */
    @Override
    public void set(Integer status) {
        redisTemplate.opsForValue().set(StatusConstant.KEY,status);
    }

    /**
     * 获取店铺的营业状态
     * @return
     */
    @Override
    public Integer get() {
        Integer status=(Integer)redisTemplate.opsForValue().get(StatusConstant.KEY);
        return status;
    }
}
