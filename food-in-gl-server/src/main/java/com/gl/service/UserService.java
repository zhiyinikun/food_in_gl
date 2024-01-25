package com.gl.service;

import com.gl.dto.UserLoginDTO;
import com.gl.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wechatLogin(UserLoginDTO userLoginDTO);
}
