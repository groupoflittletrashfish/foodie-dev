package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户ID查找用户地址列表
     *
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 添加用户地址
     *
     * @param addressBO
     */
    public void addNewUserAdress(AddressBO addressBO);

}
