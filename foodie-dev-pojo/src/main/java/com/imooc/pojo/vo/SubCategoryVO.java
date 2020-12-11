package com.imooc.pojo.vo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description: 三级分类VO
 * @author: noname
 * @create: 2020-12-11 17:56
 **/

@Data
public class SubCategoryVO {
    private Integer subId;
    private String subName;
    private String subType;
    private Integer subFatherId;
}