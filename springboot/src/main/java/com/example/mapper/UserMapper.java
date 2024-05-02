package com.example.mapper;

/*
 *选择Interface/接口：Java基础概念的接口
 */


import com.example.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {


    /**
     * 增
     */
    void insert(User user);
//    建议在resources-mapper中的.xml文件里去写SQL

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(User user);

    /**
     * 根据ID查询
     */
    User selectById(Integer id);

    /**
     * 查询所有
     */
    List<User> selectAll(User user);

    @Select("select * from user where username = #{username}")
    User selectByUserName(String username);
}
