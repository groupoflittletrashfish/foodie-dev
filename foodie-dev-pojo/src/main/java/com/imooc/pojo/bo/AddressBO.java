package com.imooc.pojo.bo;

import lombok.Data;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-17 15:25
 **/

@Data
public class AddressBO {
    private String addressId;
    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
}