package com.imooc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: foodie-dev
 * @description: 最新商品VO
 * @author: noname
 * @create: 2020-12-11 17:56
 **/

@Data
public class NewItemsVO {
    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;
    private List<SimpleItemVO> simpleItemList;
}