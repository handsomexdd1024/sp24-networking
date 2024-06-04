package com.example.controller;

import com.example.common.Result;
import com.example.entity.StationGoods;
import com.example.service.StationGoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/station-goods")
public class StationGoodsController {

    @Resource
    private StationGoodsService stationGoodsService;

    @PostMapping("/add")
    public Result add(@RequestBody StationGoods stationGoods){
        stationGoodsService.add(stationGoods);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        stationGoodsService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        stationGoodsService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody StationGoods stationGoods) {
        stationGoodsService.updateById(stationGoods);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        StationGoods stationGoods = stationGoodsService.selectById(id);
        return Result.success(stationGoods);
    }

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<StationGoods> list = stationGoodsService.selectAll();
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(StationGoods stationGoods,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<StationGoods> page = stationGoodsService.selectPage(stationGoods, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/selectByStationId/{stationId}")
    public Result selectByStationId(@PathVariable Integer stationId) {
        List<StationGoods> list = stationGoodsService.selectByStationId(stationId);
        return Result.success(list);
    }

}
