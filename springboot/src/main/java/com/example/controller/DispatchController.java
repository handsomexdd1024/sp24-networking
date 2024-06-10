package com.example.controller;

import com.example.common.Result;
import com.example.service.DispatchService;
import com.example.dto.Operation;
import com.example.dto.DispatchResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    @Resource
    private DispatchService dispatchService;

    @PostMapping("/simulate")
    public Result simulateDispatch(@RequestParam("targetStationId") int targetStationId,
                                   @RequestParam("goodsName") String goodsName,
                                   @RequestParam("quantity") int quantity) {
        try {
            dispatchService.loadData();
            DispatchResult result = dispatchService.simulateDispatch(targetStationId, goodsName, quantity);
            List<Operation> operations = dispatchService.getOperations();
            return Result.success(new Object[]{result, operations});
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细的错误信息
            return Result.error();
        }
    }






}
