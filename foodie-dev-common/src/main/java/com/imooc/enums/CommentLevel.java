package com.imooc.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommentLevel {
    GOOD(1, "好评"),
    NORMAL(2, "中评"),
    BAD(3, "差评");

    public final Integer type;
    public final String value;
}
