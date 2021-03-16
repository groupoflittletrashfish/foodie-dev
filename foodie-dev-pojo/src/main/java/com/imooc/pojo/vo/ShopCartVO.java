package com.imooc.pojo.vo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-16 18:00
 **/
@Data
public class ShopCartVO {
    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private String priceDiscount;
    private String priceNormal;
}