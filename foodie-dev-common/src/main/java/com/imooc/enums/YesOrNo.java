package com.imooc.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum YesOrNo {
    NO(0, "否"),
    YES(1, "是");

    public final Integer type;
    public final String value;
}
