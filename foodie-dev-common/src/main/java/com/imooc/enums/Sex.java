package com.imooc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sex {

    WOMAN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    private Integer type;
    private String value;
}
