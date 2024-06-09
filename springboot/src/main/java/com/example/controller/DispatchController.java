package com.example.controller;

import com.example.common.Result;
import com.example.service.DispatchService;
import com.example.dto.DispatchResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    @Resource
    private DispatchService dispatchService;

    @PostMapping("/simulate")
    public Result simulateDispatch(@RequestParam int targetStationId,
                                   @RequestParam String goodsName,
                                   @RequestParam int quantity) {
        try {
            dispatchService.loadData();
            DispatchResult result = dispatchService.simulateDispatch(targetStationId, goodsName, quantity);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error();
        }
    }
}
