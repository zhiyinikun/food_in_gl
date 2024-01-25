package com.gl.service.impl;

import com.gl.constant.StatusConstant;
import com.gl.service.ShopService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShopServiceImpl implements ShopService {

    @Resource
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
