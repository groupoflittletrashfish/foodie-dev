package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-17 15:07
 **/
@RestController
@RequestMapping("/orders")
@ApiIgnore
@Slf4j
@Api(value = "订单相关API", tags = {"订单相关API"})
public class OrdersController {

    @Resource
    private OrderService orderService;

    @ApiOperation("用户下单")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO) {
        if (!Objects.equals(submitOrderBO.getPayMethod(), PayMethod.WEIXIN.type) &&
                !Objects.equals(submitOrderBO.getPayMethod(), PayMethod.ALIPAY.type)) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }
        //1.创建订单
        orderService.createOrder(submitOrderBO);
        //2.创建订单以后移除购物车中已结算的商品

        //3.向支付中心发送当前订单，用于保存支付中心的订单
        return IMOOCJSONResult.ok();
    }
}