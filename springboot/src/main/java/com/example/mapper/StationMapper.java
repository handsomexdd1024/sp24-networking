package com.example.mapper;

import com.example.entity.Station;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 操作station相关数据接口
*/
public interface StationMapper {

    /**
      * 新增
    */
    int insert(Station station);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Station station);

    /**
      * 根据ID查询
    */
    Station selectById(Integer id);

    /**
      * 查询所有
    */
    List<Station> selectAll(Station station);

    /**
     * 可用数量
     */
    int getAvailable();

    /**
     * 不可用数量
     */
    int getDisavailable();

}