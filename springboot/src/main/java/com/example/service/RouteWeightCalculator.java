package com.example.service;

import com.example.entity.Route;
import com.example.entity.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.common.Constants.*;

@Service
public class RouteWeightCalculator {

    @Autowired
    private StationService stationService;

    @Autowired
    private RouteService routeService;

    public List<Station> getAllStations() {
        Station station = new Station();
        station.setDisableFlag("0");
        return stationService.selectAll(station);
    }

    public List<Route> getAllRoutes() {
        Route route = new Route();
        route.setDisableFlag("0");
        return routeService.selectAll(route);
    }

    public double calculateWeight(Route route) {
        Station fromStation = stationService.selectById(route.getFromStationId());
        Station toStation = stationService.selectById(route.getToStationId());

        double distance = calculateDistance(fromStation.getLatitude(), fromStation.getLongitude(),
                toStation.getLatitude(), toStation.getLongitude());

        double speed;
        switch (route.getRouteType()) {
            case "flight":
                speed = FLIGHT_SPEED;
                break;
            case "rail":
                speed = RAIL_SPEED;
                break;
            case "road":
                speed = ROAD_SPEED;
                break;
            default:
                throw new IllegalArgumentException("Unknown route type: " + route.getRouteType());
        }

        return distance / speed;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
