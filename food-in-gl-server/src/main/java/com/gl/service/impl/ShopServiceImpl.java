package com.gl.service.impl;

import com.gl.constant.StatusConstant;
import com.gl.service.ShopService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Resource
    RedisTemplate redisTemplate;
    /**
     * 设置店铺的营业状态
     * @param status
     */
    @Override
    public void set(Integer status) {
        try{
            redisTemplate.opsForValue().set(StatusConstant.KEY,status);
        }catch (Exception e){
            log.info("为启动Redis");
        }

    }

    /**
     * 获取店铺的营业状态
     * @return
     */
    @Override
    public Integer get() {
        try {
            Integer status=(Integer)redisTemplate.opsForValue().get(StatusConstant.KEY);
            return status;
        }catch (Exception e){
            log.info("为启动Redis");
        }
        return 0;
    }
}
