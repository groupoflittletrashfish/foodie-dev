package com.imooc.pojo.bo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description: 用于创建订单的BO对象
 * @author: noname
 * @create: 2020-12-18 15:46
 **/

@Data
public class SubmitOrderBO {

    private String userId;
    private String itemSpecIds;
    private String addressId;
    private Integer payMethod;
    private String leftMsg;
}