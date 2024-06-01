package com.example.mapper;

import com.example.entity.Route;

import java.util.List;

/**
 * 操作route相关数据接口
*/
public interface RouteMapper {

    /**
      * 新增
    */
    int insert(Route route);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Route route);

    /**
      * 根据ID查询
    */
    Route selectById(Integer id);

    /**
      * 查询所有
    */
    List<Route> selectAll(Route route);

}