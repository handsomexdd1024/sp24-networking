package com.example.service;


import com.example.entity.Route;
import com.example.mapper.RouteMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service

public class RouteService {

    @Resource
    private RouteMapper routeMapper;

    public void add(Route route) {
        if (route != null) {
            routeMapper.insert(route);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        routeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            routeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Route route) {
        routeMapper.updateById(route);
    }

    /**
     * 根据ID查询
     */
    public Route selectById(Integer id) {
        return routeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Route> selectAll(Route route) {
        return routeMapper.selectAll(route);
    }

    /**
     * 分页查询
     */
    public PageInfo<Route> selectPage(Route route, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Route> list = routeMapper.selectAll(route);
        return PageInfo.of(list);
    }

}
