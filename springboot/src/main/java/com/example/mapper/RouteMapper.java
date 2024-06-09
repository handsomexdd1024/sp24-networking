package com.example.mapper;

import com.example.entity.Route;
import org.apache.ibatis.annotations.Param;

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
    /**
     * 查询所有带关键字
     */
    List<Route> selectAllWithKeyWord(@Param("route") Route route, @Param("keyword") String keyword);
    /**
     * 查询是否有重复
     */
    Route selectByFromToType(@Param("fromStationId") Integer fromStationId, @Param("toStationId") Integer toStationId, @Param("routeType") String routeType);

    int getRoad();

    int getRail();

    int getFlight();
}