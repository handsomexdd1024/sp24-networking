package com.example.dto;

import java.util.ArrayList;
import java.util.List;

public class DispatchResult {

    private List<String> logs = new ArrayList<>();
    private int totalDispatched;

    public void addLog(String log) {
        logs.add(log);
    }

    public List<String> getLogs() {
        return logs;
    }

    public int getTotalDispatched() {
        return totalDispatched;
    }

    public void setTotalDispatched(int totalDispatched) {
        this.totalDispatched = totalDispatched;
    }
}
