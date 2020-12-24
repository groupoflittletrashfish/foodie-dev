package com.imooc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: foodie-dev
 * @description: 二级分类VO
 * @author: noname
 * @create: 2020-12-11 17:53
 **/

@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
}