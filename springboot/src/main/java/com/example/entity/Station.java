package com.example.entity;


/**
 * 站点
 */
public class Station {
    private Integer id;
    /** 名称 */
    private String name;
    private Double longitude;
    private Double latitude;
    private int storage;
    private boolean disableFlag;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public boolean isDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }
}
