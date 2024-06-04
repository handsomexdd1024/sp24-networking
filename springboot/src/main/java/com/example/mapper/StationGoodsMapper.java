package com.example.mapper;

import com.example.entity.StationGoods;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StationGoodsMapper {

    int insert(StationGoods stationGoods);

    int deleteById(Integer id);

    int updateById(StationGoods stationGoods);

    StationGoods selectById(Integer id);

    List<StationGoods> selectAll();

    List<StationGoods> selectByStationId(@Param("stationId") Integer stationId);
}
