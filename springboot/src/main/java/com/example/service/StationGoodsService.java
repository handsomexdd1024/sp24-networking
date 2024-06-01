package com.example.service;

import com.example.entity.StationGoods;
import com.example.mapper.StationGoodsMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StationGoodsService {

    @Resource
    private StationGoodsMapper stationGoodsMapper;

    public void add(StationGoods stationGoods) {
        if (stationGoods != null) {
            stationGoodsMapper.insert(stationGoods);
        }
    }

    public void deleteById(Integer id) {
        stationGoodsMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            stationGoodsMapper.deleteById(id);
        }
    }

    public void updateById(StationGoods stationGoods) {
        stationGoodsMapper.updateById(stationGoods);
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
}
