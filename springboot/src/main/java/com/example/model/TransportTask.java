package com.example.model;

import java.util.ArrayList;

/**
 * Ongoing transportation task on a certain route.
 */
public class TransportTask {
    private final ArrayList<GoodsItem> load;
    private int time;
    private Route route;

    public TransportTask(
            ArrayList<GoodsItem> loadList,
            Route route
    ) {
        this.load = loadList;
        this.route = route;
    }
}
