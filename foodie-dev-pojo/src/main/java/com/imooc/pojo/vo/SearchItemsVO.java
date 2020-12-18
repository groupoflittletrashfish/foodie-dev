package com.imooc.pojo.vo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description: 用于展示商品搜索结果的VO
 * @author: noname
 * @create: 2020-12-11 17:53
 **/

@Data
public class SearchItemsVO {
    private String itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer price;
}