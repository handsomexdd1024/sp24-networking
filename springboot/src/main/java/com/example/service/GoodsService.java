package com.example.service;


import com.example.entity.Goods;
import com.example.entity.GoodsTypeNum;
import com.example.entity.Station;
import com.example.entity.StationGoods;
import com.example.mapper.GoodsMapper;
import com.example.mapper.StationMapper;
import com.example.mapper.StationGoodsMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service

public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private StationGoodsMapper stationGoodsMapper;

    @Resource
    private StationMapper stationMapper;

    public int add(Goods goods) {
        if (goods != null) {
            goodsMapper.insert(goods);
            return goods.getId();  // 返回新添加货物的ID
        }
        return -1;
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        // 查找与此货物关联的station_goods记录
        StationGoods stationGoods = stationGoodsMapper.selectByGoodsId(id);
        if (stationGoods != null) {
            // 更新站点的存储量
            Station station = stationMapper.selectById(stationGoods.getStationId());
            station.setStorage(station.getStorage() + stationGoods.getQuantity());
            stationMapper.updateById(station);
            // 删除station_goods记录
            stationGoodsMapper.deleteById(stationGoods.getId());
        }
        // 删除goods记录
        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            deleteById(id); // 重用单个删除方法
        }
    }

    /**
     * 修改
     */
    @Transactional
    public void updateById(Goods goods, Integer newStationId, Integer newQuantity) {
        goodsMapper.updateById(goods);
        StationGoods stationGoods = stationGoodsMapper.selectByGoodsId(goods.getId());
        if (stationGoods != null) {
            Station oldStation = stationMapper.selectById(stationGoods.getStationId());
            oldStation.setStorage(oldStation.getStorage() + stationGoods.getQuantity());
            stationMapper.updateById(oldStation);
            stationGoodsMapper.deleteById(stationGoods.getId());
        }
        StationGoods newStationGoods = new StationGoods();
        newStationGoods.setStationId(newStationId);
        newStationGoods.setGoodsId(goods.getId());
        newStationGoods.setQuantity(newQuantity);
        stationGoodsMapper.insert(newStationGoods);

        Station newStation = stationMapper.selectById(newStationId);
        newStation.setStorage(newStation.getStorage() - newQuantity);
        stationMapper.updateById(newStation);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);
    }

    /**
     * 分页查询带站点过滤
     */
    public PageInfo<Goods> selectPageWithStation(Goods goods, Integer pageNum, Integer pageSize, Integer stationId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAllWithStation(goods, stationId);
        return PageInfo.of(list);
    }

    public List<String> getCategories() {
        return goodsMapper.getCategories();
    }


    public List<GoodsTypeNum> getTypeAndNum(){
        return goodsMapper.getTypeAndNum();
    }



}
