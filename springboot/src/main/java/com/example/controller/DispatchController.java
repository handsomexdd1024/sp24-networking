package com.example.controller;

import com.example.common.Result;
import com.example.service.DispatchService;
import com.example.dto.Operation;
import com.example.dto.DispatchResult;
import com.example.graph.graph_base.Node;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    @Resource
    private DispatchService dispatchService;

    @PostMapping("/simulateFastest")
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

    @PostMapping("/simulateEconomic")
    public Result simulateEconomicDispatch(@RequestParam("targetStationId") int targetStationId,
                                           @RequestParam("goodsName") String goodsName,
                                           @RequestParam("quantity") int quantity) {
        try {
            dispatchService.loadData();
            DispatchResult result = dispatchService.simulateEconomicDispatch(targetStationId, goodsName, quantity);
            List<Operation> operations = dispatchService.getOperations();
            return Result.success(new Object[]{result, operations});
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细的错误信息
            return Result.error();
        }
    }

    @PostMapping("/findShortestPathUsingAntColony")
    public Result findShortestPathUsingAntColony(@RequestParam("startCity") String startCity) {
        try {
            List<Node> path = dispatchService.findShortestPathUsingAntColony(startCity);
            return Result.success(path);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }

    @PostMapping("/simulateAntColony")
    public Result simulateAntColonyDispatch(@RequestParam("targetStationId") int targetStationId,
                                            @RequestParam("goodsName") String goodsName,
                                            @RequestParam("quantity") int quantity) {
        try {
            DispatchResult result = dispatchService.simulateAntColonyDispatch(targetStationId, goodsName, quantity);
            List<Operation> operations = dispatchService.getOperations();
            return Result.success(new Object[]{result, operations});
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细的错误信息
            return Result.error();
        }
    }
}