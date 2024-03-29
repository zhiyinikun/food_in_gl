package com.gl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gl.constant.MessageConstant;
import com.gl.constant.WeChatConstant;
import com.gl.dto.UserLoginDTO;
import com.gl.entity.User;
import com.gl.exception.LoginFailedException;
import com.gl.mapper.UserMapper;
import com.gl.properties.WeChatProperties;
import com.gl.service.UserService;
import com.gl.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;


    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wechatLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当地微信用户的openid 看微信开发小程序的接口文档
        HashMap<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");

        String json = HttpClientUtil.doGet(WeChatConstant.WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        //判晰openid是否为空，如果为空表示登灵失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                     .openid(openid)
                     .createTime(LocalDateTime.now())
                     .build();
            userMapper.insert(user);
        }
        //返回这个用户对象
        return user;
    }
}
