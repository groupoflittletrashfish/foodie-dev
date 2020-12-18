package com.imooc.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: foodie-dev
 * @description: 六个最新商品的简单数据类型
 * @author: noname
 * @create: 2020-12-14 18:05
 **/
@Data
public class SimpleItemVO implements Serializable {

    private String itemId;
    private String itemName;
    private String itemUrl;
}