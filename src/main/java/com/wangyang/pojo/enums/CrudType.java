package com.wangyang.pojo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CrudType {
    ARTICLE(0,"GSE"),GSM(1,"GSM"),GPL(2,"GPL"),CODE(3,"CODE");
    private final  String name;
    private final   int code;

    CrudType(int code, String name) {
        this.name = name;
        this.code=code;
    }
    public Integer getCode() {
        return code;
    }
    @JsonValue
    public String getValue() {
        return name;
    }
}
