package com.example.controller;

import com.example.common.Result;
import com.example.entity.Station;
import com.example.service.StationService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.common.City;
import com.example.common.Constants;

/**
 * 用户管理前端操作接口
 **/
@RestController
@RequestMapping("/station")

/**
 * Controller是数据的入口
 * Service处理业务逻辑
 **/

public class StationController {

    //    引入Service
    @Resource
    private StationService stationService;


    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Station station){
        stationService.add(station);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        stationService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        stationService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Station station) {
        stationService.updateById(station);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Station station = stationService.selectById(id);
        return Result.success(station);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Station station ) {
        List<Station> list = stationService.selectAll(station);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Station station,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Station> page = stationService.selectPage(station, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取所有城市信息常量
     */
    @GetMapping("/allCities")
    public Result getAllCities() {
        return Result.success(Constants.ALL_CITIES);
    }

}
