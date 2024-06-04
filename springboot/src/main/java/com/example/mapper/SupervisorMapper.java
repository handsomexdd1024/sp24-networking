package com.example.mapper;

import com.example.entity.Supervisor;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作supervisor相关数据接口
*/
public interface SupervisorMapper {

    /**
      * 新增
    */
    int insert(Supervisor supervisor);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Supervisor supervisor);

    /**
      * 根据ID查询
    */
    Supervisor selectById(Integer id);

    /**
      * 查询所有
    */
    List<Supervisor> selectAll(Supervisor supervisor);

    @Select("select * from supervisor where username = #{username}")
    Supervisor selectByUsername(String username);
}