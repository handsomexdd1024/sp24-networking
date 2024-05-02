package com.example.common.enums;

public enum MemberEnum {

    YES("会员"),
    NO("非会员"),
    ;
    public String info;

//    构造方法
    MemberEnum(String info) {
        this.info = info;
    }
}
