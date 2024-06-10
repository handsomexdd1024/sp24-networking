package com.example.dispatch;

public class PathNode {
    private int stationId;
    private double totalTime;
    private PathNode previous;
    private String routeType; // 添加路径类型字段
    private int dispatchedQuantity;

    public PathNode() {}

    public PathNode(int stationId, double totalTime, PathNode previous, String routeType, int dispatchedQuantity) {
        this.stationId = stationId;
        this.totalTime = totalTime;
        this.previous = previous;
        this.routeType = routeType;
        this.dispatchedQuantity = dispatchedQuantity;
    }


    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public PathNode getPrevious() {
        return previous;
    }

    public void setPrevious(PathNode previous) {
        this.previous = previous;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public int getDispatchedQuantity() {
        return dispatchedQuantity;
    }

    public void setDispatchedQuantity(int dispatchedQuantity) {
        this.dispatchedQuantity = dispatchedQuantity;
    }

    @Override
    public String toString() {
        return "PathNode{" +
                "stationId=" + stationId +
                ", totalTime=" + totalTime +
                ", previous=" + previous +
                '}';
    }
}
