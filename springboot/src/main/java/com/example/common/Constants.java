package com.example.common;

import java.util.Arrays;
import java.util.List;

public interface Constants {

    String TOKEN = "token";

    String USER_DEFAULT_PASSWORD = "123";

    List<City> ALL_CITIES = Arrays.asList(
            new City("上海", 31.2304, 121.4737),
            new City("北京", 39.9042, 116.4074),
            new City("广州", 23.1291, 113.2644),
            new City("深圳", 22.5431, 114.0579),
            new City("成都", 30.5728, 104.0668),
            new City("重庆", 29.5637, 106.5507),
            new City("杭州", 30.2741, 120.1551),
            new City("武汉", 30.5931, 114.3054),
            new City("苏州", 31.2989, 120.5853),
            new City("西安", 34.3416, 108.9398),
            new City("南京", 32.0603, 118.7969),
            new City("长沙", 28.2282, 112.9388),
            new City("天津", 39.3434, 117.3616),
            new City("郑州", 34.7466, 113.6254),
            new City("东莞", 23.0205, 113.7518),
            new City("青岛", 36.0671, 120.3826),
            new City("昆明", 25.0406, 102.7123),
            new City("宁波", 29.8683, 121.5439),
            new City("合肥", 31.8206, 117.2272),
            new City("佛山", 23.0215, 113.1214),
            new City("沈阳", 41.8057, 123.4328),
            new City("无锡", 31.5747, 120.3017),
            new City("济南", 36.6512, 117.1201),
            new City("福州", 26.0745, 119.2965),
            new City("温州", 27.9949, 120.6994),
            new City("哈尔滨", 45.8038, 126.5349),
            new City("石家庄", 38.0428, 114.5149),
            new City("大连", 38.9140, 121.6147),
            new City("南宁", 22.8170, 108.3669),
            new City("泉州", 24.8741, 118.6757),
            new City("金华", 29.0791, 119.6474),
            new City("贵阳", 26.6470, 106.6302),
            new City("常州", 31.8107, 119.9737),
            new City("长春", 43.8171, 125.3235),
            new City("南昌", 28.6820, 115.8579),
            new City("南通", 31.9802, 120.8943),
            new City("嘉兴", 30.7452, 120.7555),
            new City("徐州", 34.2044, 117.2858),
            new City("惠州", 23.1102, 114.4168),
            new City("太原", 37.8706, 112.5489),
            new City("台州", 28.6564, 121.4208),
            new City("绍兴", 30.0303, 120.5802),
            new City("保定", 38.8739, 115.4646),
            new City("中山", 22.5176, 113.3926),
            new City("潍坊", 36.7069, 119.1618),
            new City("临沂", 35.1046, 118.3564),
            new City("珠海", 22.2707, 113.5767),
            new City("烟台", 37.4638, 121.4479)
    );

    // 路径运输常量
    double PLANE_SPEED = 800.0; // km/h
    double PLANE_COST = 5.0; // 元/吨/千米

    double TRAIN_SPEED = 60.0; // km/h
    double TRAIN_COST = 1.0; // 元/吨/千米

    double ROAD_SPEED = 100.0; // km/h
    double ROAD_COST = 2.0; // 元/吨/千米
}
