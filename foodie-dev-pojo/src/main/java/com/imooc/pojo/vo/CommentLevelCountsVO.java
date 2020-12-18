package com.imooc.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: foodie-dev
 * @description: 用于展示商品评价数的VO
 * @author: noname
 * @create: 2020-12-15 17:17
 **/
@Data
public class CommentLevelCountsVO implements Serializable {

    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
}