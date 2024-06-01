package com.example.service;


import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Station;
import com.example.exception.CustomException;
import com.example.mapper.StationMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service

public class StationService {

    @Resource
    private StationMapper stationMapper;

    public void add(Station station) {
        if (station != null) {
            stationMapper.insert(station);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        stationMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            stationMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Station station) {
        stationMapper.updateById(station);
    }

    /**
     * 根据ID查询
     */
    public Station selectById(Integer id) {
        return stationMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Station> selectAll(Station station) {
        return stationMapper.selectAll(station);
    }

    /**
     * 分页查询
     */
    public PageInfo<Station> selectPage(Station station, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Station> list = stationMapper.selectAll(station);
        return PageInfo.of(list);
    }

}
