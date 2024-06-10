package com.example.service;

import com.example.entity.Route;
import com.example.entity.Station;
import com.example.entity.StationGoods;
import com.example.dto.DispatchResult;
import com.example.mapper.StationGoodsMapper;
import com.example.mapper.StationMapper;
import com.example.mapper.RouteMapper;
import com.example.common.Constants;

import com.example.dispatch.PathNode;
import com.example.dto.Operation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DispatchService {



    @Resource
    private StationMapper stationMapper;
    @Resource
    private RouteMapper routeMapper;
    @Resource
    private StationGoodsMapper stationGoodsMapper;

    private Map<Integer, Station> stationsMap;
    private Map<Integer, List<Route>> adjacencyList;
    private List<StationGoods> stationGoodsList;
    private List<Operation> operations;

    public void loadData() {
        stationsMap = stationMapper.selectAll(new Station())
                .stream()
                .filter(station -> "0".equals(station.getDisableFlag()))
                .collect(Collectors.toMap(Station::getId, station -> station));

        adjacencyList = new HashMap<>();
        for (Route route : routeMapper.selectAll(new Route())) {
            if ("0".equals(route.getDisableFlag())) {
                adjacencyList.computeIfAbsent(route.getToStationId(), k -> new ArrayList<>()).add(route);
            }
        }

        stationGoodsList = stationGoodsMapper.selectAll();
    }


    public DispatchResult simulateDispatch(int targetStationId, String goodsName, int quantity) {
        DispatchResult result = new DispatchResult();
        List<PathNode> initialPaths = findFastestPaths(targetStationId);

        // 打印站点货物数据
        System.out.println("Station goods list: " + stationGoodsList);

        System.out.println("Initial paths: " + initialPaths);
        System.out.println("Initial name: " + goodsName);
        int remainingQuantity = dispatchFromPaths(initialPaths, targetStationId, goodsName, quantity, result);
        System.out.println("Remaining quantity after dispatchFromPaths: " + remainingQuantity);
        System.out.println("DispatchResult after dispatchFromPaths: " + result);

        if (remainingQuantity > 0) {
            remainingQuantity = dispatchViaTransfer(targetStationId, goodsName, remainingQuantity, result);
            System.out.println("Remaining quantity after dispatchViaTransfer: " + remainingQuantity);
            System.out.println("DispatchResult after dispatchViaTransfer: " + result);
        }

        result.setTotalDispatched(quantity - remainingQuantity);
        System.out.println("Final DispatchResult: " + result);

        return result;
    }




    private int dispatchFromPaths(List<PathNode> paths, int targetStationId, String goodsName, int quantity, DispatchResult result) {
        int remainingQuantity = quantity;
        for (PathNode path : paths) {
            int stationId = path.getStationId();
            System.out.println("Checking station: " + stationId);
            List<StationGoods> goodsList = getGoodsAtStation(stationId, goodsName);
            System.out.println("GOD station: " + goodsList);
            for (StationGoods goods : goodsList) {
                if (remainingQuantity <= 0) break;

                int availableQuantity = goods.getQuantity();
                if (availableQuantity >= remainingQuantity) {
                    result.addLog("从站点 " + stationId + " 调度 " + remainingQuantity + " 吨货物到站点 " + targetStationId);
                    updateGoodsQuantity(goods, remainingQuantity);
                    remainingQuantity = 0;
                    break;
                } else {
                    result.addLog("从站点 " + stationId + " 调度 " + availableQuantity + " 吨货物到站点 " + targetStationId);
                    updateGoodsQuantity(goods, availableQuantity);
                    remainingQuantity -= availableQuantity;
                }
            }
        }
        return remainingQuantity;
    }



    private int dispatchViaTransfer(int targetStationId, String goodsName, int remainingQuantity, DispatchResult result) {
        List<PathNode> initialPaths = findFastestPaths(targetStationId);
        System.out.println("Initial paths for transfer: " + initialPaths);

        for (PathNode path : initialPaths) {
            if (remainingQuantity <= 0) break;
            int transferStationId = path.getStationId();
            System.out.println("Checking transfer station: " + transferStationId);
            List<PathNode> transferPaths = findFastestPaths(transferStationId);
            System.out.println("Transfer paths from station " + transferStationId + ": " + transferPaths);

            remainingQuantity = dispatchFromPaths(transferPaths, transferStationId, goodsName, remainingQuantity, result);

            if (remainingQuantity <= 0) {
                break;
            }
        }

        return remainingQuantity;
    }


    private List<StationGoods> getGoodsAtStation(int stationId, String goodsName) {
        List<StationGoods> filteredGoods = stationGoodsList.stream()
                .filter(g -> {
                    boolean stationMatch = Objects.equals(g.getStationId(), stationId);
                    boolean nameMatch = goodsName.equals(Optional.ofNullable(g.getName()).orElse(""));
                    boolean categoryMatch = goodsName.equals(Optional.ofNullable(g.getCategory()).orElse(""));



                    return stationMatch && (nameMatch || categoryMatch);
                })
                .sorted(Comparator.comparing(StationGoods::getId).reversed())
                .collect(Collectors.toList());

        System.out.println("Filtered goods at station " + stationId + " for goodsName " + goodsName + ": " + filteredGoods);
        return filteredGoods;
    }



    @Transactional
    public void updateGoodsQuantity(StationGoods goods, int quantity) {
        if (operations == null) {
            operations = new ArrayList<>();
        }

        goods.setQuantity(goods.getQuantity() - quantity);
        if (goods.getQuantity() <= 0) {
            stationGoodsMapper.deleteById(goods.getId());
            operations.add(new Operation("deleteGoods", goods));
        } else {
            stationGoodsMapper.updateById(goods);
            operations.add(new Operation("updateGoods", goods));
        }

        Station station = stationsMap.get(goods.getStationId());
        station.setStorage(station.getStorage() - quantity);
        stationMapper.updateById(station);
    }



    public List<Operation> getOperations() {
        return operations;
    }

    private List<PathNode> findFastestPaths(int targetStationId) {
        PriorityQueue<PathNode> queue = new PriorityQueue<>(Comparator.comparingDouble(PathNode::getTotalTime));
        Map<Integer, PathNode> bestPaths = new HashMap<>();

        PathNode startNode = new PathNode();
        startNode.setStationId(targetStationId);
        startNode.setTotalTime(0);
        queue.add(startNode);
        bestPaths.put(targetStationId, startNode);

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            for (Route route : adjacencyList.getOrDefault(current.getStationId(), Collections.emptyList())) {
                if (!route.isEnabled()) continue;

                int nextStationId = route.getFromStationId();
                double distance = calculateDistance(stationsMap.get(current.getStationId()), stationsMap.get(nextStationId));
                double additionalTime = distance / getSpeed(route.getRouteType());

                double newTotalTime = current.getTotalTime() + additionalTime;
                PathNode nextNode = bestPaths.get(nextStationId);
                if (nextNode == null || newTotalTime < nextNode.getTotalTime()) {
                    nextNode = new PathNode();
                    nextNode.setStationId(nextStationId);
                    nextNode.setTotalTime(newTotalTime);
                    nextNode.setPrevious(current);
                    queue.add(nextNode);
                    bestPaths.put(nextStationId, nextNode);
                }
            }
        }

        return new ArrayList<>(bestPaths.values());
    }

    private double calculateDistance(Station from, Station to) {
        double lat1 = from.getLatitude();
        double lon1 = from.getLongitude();
        double lat2 = to.getLatitude();
        double lon2 = to.getLongitude();
        double R = 6371; // 地球半径，单位：公里
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // 单位：公里
        return distance;
    }

    private double getSpeed(String routeType) {
        switch (routeType) {
            case "flight":
                return Constants.FLIGHT_SPEED;
            case "rail":
                return Constants.RAIL_SPEED;
            case "road":
                return Constants.ROAD_SPEED;
            default:
                throw new IllegalArgumentException("Unknown route type: " + routeType);
        }
    }
}
