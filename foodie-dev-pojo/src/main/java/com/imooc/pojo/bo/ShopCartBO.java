package com.imooc.pojo.bo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-16 17:07
 **/

@Data
public class ShopCartBO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private String priceDiscount;
    private String priceNormal;
}