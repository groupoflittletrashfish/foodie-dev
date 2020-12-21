package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import com.imooc.util.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-17 15:07
 **/
@RestController
@RequestMapping("/address")
@ApiIgnore
@Slf4j
@Api(value = "地址相关API", tags = {"地址相关API"})
public class AddressController {

    @Resource
    private AddressService addressService;


    @ApiOperation("根据用户ID查找用户收获地址")
    @PostMapping("/list")
    public IMOOCJSONResult list(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        return IMOOCJSONResult.ok(addressService.queryAll(userId));
    }


    @ApiOperation("用户新增地址")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestBody AddressBO addressBO) {
        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.addNewUserAdress(addressBO);
        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return IMOOCJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return IMOOCJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
        }

        return IMOOCJSONResult.ok();
    }


    @ApiOperation("用户修改地址")
    @PostMapping("/update")
    public IMOOCJSONResult update(@RequestBody AddressBO addressBO) {
        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return IMOOCJSONResult.errorMsg("修改地址错误：addressId不能为空");
        }
        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.updateUserAddress(addressBO);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("用户删除地址")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String userId, @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.deleteUserAddress(userId, addressId);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("用户设置默认地址")
    @PostMapping("/setDefalut")
    public IMOOCJSONResult setDefault(@RequestParam String userId, @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.updateUserAddressToBeDefault(userId, addressId);
        return IMOOCJSONResult.ok();
    }
}