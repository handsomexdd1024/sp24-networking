package com.example.controller;

import com.example.common.Result;
import com.example.entity.Route;
import com.example.service.RouteService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理前端操作接口
 **/
@RestController
@RequestMapping("/route")

/**
 * Controller是数据的入口
 * Service处理业务逻辑
 **/

public class RouteController {

    //    引入Service
    @Resource
    private RouteService routeService;


    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Route route){
        routeService.add(route);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        routeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        routeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Route route) {
        routeService.updateById(route);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Route route = routeService.selectById(id);
        return Result.success(route);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Route route ) {
        List<Route> list = routeService.selectAll(route);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Route route,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Route> page = routeService.selectPage(route, pageNum, pageSize);
        return Result.success(page);
    }
    /**
     * 分页查询带关键字
     */
    @GetMapping("/selectPageWithKeyWord")
    public Result selectPageWithKeyWord(Route route,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String keyword) {
        PageInfo<Route> page = routeService.selectPageWithKeyWord(route, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取路线类型的数量
     */
    @GetMapping("/getRouteTypeNum")
    public Result getRouteTypeNum() {
        List<Integer> list = new ArrayList<>();
        list.add(routeService.getRoad());
        list.add(routeService.getRail());
        list.add(routeService.getFlight());
        return Result.success(list);
    }

}
