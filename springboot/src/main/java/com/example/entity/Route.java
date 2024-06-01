package com.example.entity;


/**
 * 角色用户父类
 */
public class Route {
    private Integer id;
    private Integer fromStationId;
    private Integer toStationId;
    private String routeType;
    private String disableFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(Integer fromStationId) {
        this.fromStationId = fromStationId;
    }

    public Integer getToStationId() {
        return toStationId;
    }

    public void setToStationId(Integer toStationId) {
        this.toStationId = toStationId;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(String disableFlag) {
        this.disableFlag = disableFlag;
    }
}
