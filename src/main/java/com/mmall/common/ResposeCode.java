package com.mmall.common;

public enum ResposeCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),//需要登录
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");//参数错误
    private final int code;
    private final String desc;

    ResposeCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
