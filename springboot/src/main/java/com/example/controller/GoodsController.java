package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Goods;
import com.example.service.GoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户管理前端操作接口
 **/
@RestController
@RequestMapping("/goods")

/**
 * Controller是数据的入口
 * Service处理业务逻辑
 **/

public class GoodsController {

    //    引入Service
    @Resource
    private GoodsService goodsService;


    @PostMapping("/add")
    public Result add(@RequestBody Goods goods){
        int id = goodsService.add(goods);
        if (id != -1) {
            return Result.success(id);
        } else {
            return Result.error(ResultCodeEnum.valueOf("添加货物失败"));
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        goodsService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        goodsService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Goods goods) {
        goodsService.updateById(goods);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Goods goods = goodsService.selectById(id);
        return Result.success(goods);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Goods goods ) {
        List<Goods> list = goodsService.selectAll(goods);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Goods goods,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Goods> page = goodsService.selectPage(goods, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/selectPageWithStation")
    public Result selectPageWithStation(Goods goods,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) Integer stationId) {
        PageInfo<Goods> page = goodsService.selectPageWithStation(goods, pageNum, pageSize, stationId);
        return Result.success(page);
    }

    @GetMapping("/categories")
    public Result getCategories() {
        List<String> categories = goodsService.getCategories();
        return Result.success(categories);
    }

}
