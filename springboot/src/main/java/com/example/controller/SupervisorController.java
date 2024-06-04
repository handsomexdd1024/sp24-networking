package com.example.controller;

import com.example.common.Result;
import com.example.entity.Supervisor;
import com.example.service.SupervisorService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员前端操作接口
 **/
@RestController
@RequestMapping("/supervisor")
public class SupervisorController {

    @Resource
    private SupervisorService supervisorService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Supervisor supervisor) {
        supervisorService.add(supervisor);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        supervisorService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        supervisorService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Supervisor supervisor) {
        supervisorService.updateById(supervisor);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Supervisor supervisor = supervisorService.selectById(id);
        return Result.success(supervisor);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Supervisor supervisor ) {
        List<Supervisor> list = supervisorService.selectAll(supervisor);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Supervisor supervisor,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Supervisor> page = supervisorService.selectPage(supervisor, pageNum, pageSize);
        return Result.success(page);
    }

}