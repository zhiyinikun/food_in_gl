package com.gl.mapper;

import com.gl.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper

public interface UserMapper {
    /**
     * 查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 通过查询用户
     * @param userId
     * @return
     */

    @Select("select * from user where id =#{userId}")
    User getById(Long userId);

    /**
     * 动态查询用户数量
     * @return
     */
    Integer count(Map map);

    /**
     * 查询当天用户数量
     * @param map
     * @return
     */
    Integer countDay(Map map);
}
