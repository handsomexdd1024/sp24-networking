package com.example.service;

import com.example.entity.StationGoods;
import com.example.entity.Station;
import com.example.mapper.StationGoodsMapper;
import com.example.mapper.StationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StationGoodsService {

    @Resource
    private StationGoodsMapper stationGoodsMapper;

    @Resource
    private StationMapper stationMapper;

    public void add(StationGoods stationGoods) {
        if (stationGoods != null) {
            stationGoodsMapper.insert(stationGoods);
            // 更新站点存储量
            Station station = stationMapper.selectById(stationGoods.getStationId());
            station.setStorage(station.getStorage() - stationGoods.getQuantity());
            stationMapper.updateById(station);
        }
    }

    public void deleteById(Integer id) {
        StationGoods stationGoods = stationGoodsMapper.selectById(id);
        if (stationGoods != null) {
            stationGoodsMapper.deleteById(id);
            // 更新站点存储量
            Station station = stationMapper.selectById(stationGoods.getStationId());
            station.setStorage(station.getStorage() + stationGoods.getQuantity());
            stationMapper.updateById(station);
        }
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            deleteById(id);  // 使用已有的 deleteById 方法
        }
    }

    public void updateById(StationGoods stationGoods) {
        StationGoods oldStationGoods = stationGoodsMapper.selectById(stationGoods.getId());
        if (oldStationGoods != null) {
            stationGoodsMapper.updateById(stationGoods);
            // 更新站点存储量
            Station station = stationMapper.selectById(stationGoods.getStationId());
            station.setStorage(station.getStorage() + oldStationGoods.getQuantity() - stationGoods.getQuantity());
            stationMapper.updateById(station);
        }
    }

    public StationGoods selectById(Integer id) {
        return stationGoodsMapper.selectById(id);
    }

    public List<StationGoods> selectAll() {
        return stationGoodsMapper.selectAll();
    }

    public PageInfo<StationGoods> selectPage(StationGoods stationGoods, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<StationGoods> list = stationGoodsMapper.selectAll();
        return PageInfo.of(list);
    }

    public List<StationGoods> selectByStationId(Integer stationId) {
        return stationGoodsMapper.selectByStationId(stationId);
    }

    public StationGoods selectByGoodsId(Integer goodsId) {
        return stationGoodsMapper.selectByGoodsId(goodsId);
    }
}
