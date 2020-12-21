package com.imooc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2020/12/18.
 */
@AllArgsConstructor
@Getter
public enum PayMethod {

    WEIXIN(1, "微信"),
    ALIPAY(2, "支付宝");

    public final Integer type;
    public final String value;
}
