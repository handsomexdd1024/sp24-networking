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
//            System.out.println("Received targetStationId: " + targetStationId); // 打印接收到的参数
//            System.out.println("Received goodsName: " + goodsName);
//            System.out.println("Received quantity: " + quantity);
            dispatchService.loadData();
            DispatchResult result = dispatchService.simulateDispatch(targetStationId, goodsName, quantity);
            System.out.println("dispatchService dispatchService: " + dispatchService.getOperations());

            List<Operation> operations = dispatchService.getOperations();
            System.out.println("dispatchService result: " + Result.success(new Object[]{result, operations}));
            return Result.success(new Object[]{result, operations});
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细的错误信息
            return Result.error();
        }
    }





}
