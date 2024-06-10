package com.example.dto;

public class Operation {
    private String type;
    private Object data;

    public Operation(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
