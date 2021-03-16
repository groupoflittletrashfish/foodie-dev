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


    /**
     * 用户修改地址
     *
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);


    /**
     * 根据用户ID和地址ID删除地址
     *
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId, String addressId);


    /**
     * 修改地址为默认地址
     *
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);


    /**
     * 根据用户ID和地址ID，查询具体的用户地址对象信息
     *
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId, String addressId);

}
