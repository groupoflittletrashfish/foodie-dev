package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-17 15:12
 **/

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private Sid sid;

    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress ua = new UserAddress();
        ua.setUserId(userId);
        return userAddressMapper.select(ua);
    }

    @Override
    public void addNewUserAdress(AddressBO addressBO) {
        //判断用户是否存在地址，如果没有，则新增为默认地址
        Integer isDefault = 0;
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (CollectionUtils.isEmpty(addressList)) {
            isDefault = 1;
        }
        UserAddress newAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, newAddress);
        newAddress.setId(sid.nextShort());
        newAddress.setIsDefault(isDefault);
        newAddress.setCreatedTime(new Date());
        newAddress.setUpdatedTime(new Date());
        userAddressMapper.insert(newAddress);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();
        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, pendingAddress);
        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(new Date());
        userAddressMapper.updateByPrimaryKeySelective(pendingAddress);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        userAddressMapper.delete(address);
    }

    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        //查找默认地址，设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.YES.type);
        List<UserAddress> list = userAddressMapper.select(queryAddress);
        for (UserAddress ua : list) {
            ua.setIsDefault(YesOrNo.NO.type);
            userAddressMapper.updateByPrimaryKeySelective(ua);
        }

        //根据地址ID修改为默认地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setUserId(userId);
        defaultAddress.setId(addressId);
        defaultAddress.setIsDefault(YesOrNo.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }

    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress singleAddress = new UserAddress();
        singleAddress.setId(addressId);
        singleAddress.setUserId(userId);
        return userAddressMapper.selectOne(singleAddress);
    }
}