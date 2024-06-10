package com.example.service;

import com.example.entity.Route;
import com.example.entity.Station;
import com.example.entity.StationGoods;
import com.example.entity.Goods;
import com.example.dto.DispatchResult;
import com.example.mapper.StationGoodsMapper;
import com.example.mapper.StationMapper;
import com.example.mapper.RouteMapper;
import com.example.mapper.GoodsMapper;
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
    @Resource
    private GoodsMapper goodsMapper;

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

        int remainingQuantity = dispatchFromPaths(initialPaths, targetStationId, goodsName, quantity, result);

        if (remainingQuantity > 0) {
            remainingQuantity = dispatchViaTransfer(targetStationId, goodsName, remainingQuantity, result);
        }

        result.setTotalDispatched(quantity - remainingQuantity);

        // 计算最长时间和总成本
        double maxTime = 0;
        double totalCost = 0;
        for (PathNode path : result.getUsedPaths()) {
            if (path.getTotalTime() > maxTime) {
                maxTime = path.getTotalTime();
            }
            totalCost += calculatePathCost(path, result);
        }
        result.setMaxTime(maxTime);
        result.setTotalCost(totalCost);

        return result;
    }


    private int dispatchFromPaths(List<PathNode> paths, int targetStationId, String goodsName, int quantity, DispatchResult result) {
        int remainingQuantity = quantity;
        for (PathNode path : paths) {
            int stationId = path.getStationId();
            List<StationGoods> goodsList = getGoodsAtStation(stationId, goodsName);
            for (StationGoods goods : goodsList) {
                if (remainingQuantity <= 0) break;

                int availableQuantity = goods.getQuantity();
                if (availableQuantity > 0) {
                    result.addUsedPath(path); // 记录使用的路径

                    // 调试输出
                    System.out.println("Dispatching from Station ID: " + stationId + " to Target Station ID: " + targetStationId);
                    System.out.println("Route Type: " + path.getRouteType());
                    System.out.println("Available Quantity: " + availableQuantity);
                    System.out.println("Remaining Quantity: " + remainingQuantity);

                    if (availableQuantity >= remainingQuantity) {
                        result.addLog(generateLogMessage(path, stationId, targetStationId, remainingQuantity));
                        updateGoodsQuantity(goods, remainingQuantity);
                        remainingQuantity = 0;
                    } else {
                        result.addLog(generateLogMessage(path, stationId, targetStationId, availableQuantity));
                        updateGoodsQuantity(goods, availableQuantity);
                        remainingQuantity -= availableQuantity;
                    }
                }
            }
        }
        return remainingQuantity;
    }


    private int dispatchViaTransfer(int targetStationId, String goodsName, int remainingQuantity, DispatchResult result) {
        List<PathNode> initialPaths = findFastestPaths(targetStationId);

        for (PathNode path : initialPaths) {
            if (remainingQuantity <= 0) break;
            int transferStationId = path.getStationId();
            List<PathNode> transferPaths = findFastestPaths(transferStationId);

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

        return filteredGoods;
    }

    @Transactional
    public void updateGoodsQuantity(StationGoods stationGoods, int quantity) {
        if (operations == null) {
            operations = new ArrayList<>();
        }

        // 更新 StationGoods 表
        stationGoods.setQuantity(stationGoods.getQuantity() - quantity);
        if (stationGoods.getQuantity() <= 0) {
            stationGoodsMapper.deleteById(stationGoods.getId());
            operations.add(new Operation("deleteGoods", stationGoods));
        } else {
            stationGoodsMapper.updateById(stationGoods);
            operations.add(new Operation("updateGoods", stationGoods));
        }

        // 更新 Station 表
        Station station = stationsMap.get(stationGoods.getStationId());
        station.setStorage(station.getStorage() + quantity); // 增加仓库空余量
        stationMapper.updateById(station);

        // 更新 Goods 表
        Goods goods = goodsMapper.selectById(stationGoods.getGoodsId());
        if (goods != null) {
            goods.setQuantity(goods.getQuantity() - quantity);
            if (goods.getQuantity() <= 0) {
                goodsMapper.deleteById(goods.getId());
            } else {
                goodsMapper.updateById(goods);
            }
        }
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
                    nextNode.setRouteType(route.getRouteType()); // 记录路径类型
                    queue.add(nextNode);
                    bestPaths.put(nextStationId, nextNode);

                    // 调试输出
                    System.out.println("Selected Path: " + current.getStationId() + " -> " + nextStationId);
                    System.out.println("Route Type: " + route.getRouteType());
                    System.out.println("Distance: " + distance);
                    System.out.println("Speed: " + getSpeed(route.getRouteType()));
                    System.out.println("Total Time: " + newTotalTime);
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

    private double calculatePathCost(PathNode path, DispatchResult result) {
        double totalCost = 0;
        PathNode current = path;
        while (current.getPrevious() != null) {
            Station fromStation = stationsMap.get(current.getPrevious().getStationId());
            Station toStation = stationsMap.get(current.getStationId());
            double distance = calculateDistance(fromStation, toStation);
            Route route = getRoute(current.getPrevious().getStationId(), current.getStationId());

            if (route != null) {
                double costPerTonPerKm = getCost(route.getRouteType());
                totalCost += distance * costPerTonPerKm * result.getTotalDispatched(); // 乘以调度量计算总成本
            }
            current = current.getPrevious();
        }
        return totalCost;
    }

    private Route getRoute(int fromStationId, int toStationId) {
        return adjacencyList.getOrDefault(toStationId, Collections.emptyList()).stream()
                .filter(route -> route.getFromStationId() == fromStationId)
                .findFirst()
                .orElse(null);
    }

    private double getCost(String routeType) {
        switch (routeType) {
            case "flight":
                return Constants.FLIGHT_COST;
            case "rail":
                return Constants.RAIL_COST;
            case "road":
                return Constants.ROAD_COST;
            default:
                throw new IllegalArgumentException("Unknown route type: " + routeType);
        }
    }

    private String generateLogMessage(PathNode path, int fromStationId, int toStationId, int quantity) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("从站点 ").append(stationsMap.get(fromStationId).getName())
                .append(" 调度 ").append(quantity).append(" 吨货物到站点 ").append(stationsMap.get(toStationId).getName());

        // 添加路径信息
        PathNode current = path;
        List<String> routeDescriptions = new ArrayList<>();
        while (current.getPrevious() != null) {
            Route route = getRoute(current.getPrevious().getStationId(), current.getStationId());
            if (route != null) {
                routeDescriptions.add("经过 " + stationsMap.get(current.getPrevious().getStationId()).getName() + " > " +
                        stationsMap.get(current.getStationId()).getName() + " 的 " + route.getRouteType() + " 路线");
            }
            current = current.getPrevious();
        }

        if (!routeDescriptions.isEmpty()) {
            logMessage.append(" (").append(String.join(", ", routeDescriptions)).append(")");
        }

        // 调试输出
        System.out.println("Generated Log Message: " + logMessage.toString());

        return logMessage.toString();
    }



}
