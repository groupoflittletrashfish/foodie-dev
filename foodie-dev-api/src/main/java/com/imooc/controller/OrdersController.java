package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-17 15:07
 **/
@RestController
@RequestMapping("/orders")
@Slf4j
@Api(value = "订单相关API", tags = {"订单相关API"})
public class OrdersController extends BaseController {

    @Resource
    private OrderService orderService;
    @Resource
    private RestTemplate restTemplate;

    @ApiOperation("用户下单")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request,
                                  HttpServletResponse response) {
        if (!Objects.equals(submitOrderBO.getPayMethod(), PayMethod.WEIXIN.type) &&
                !Objects.equals(submitOrderBO.getPayMethod(), PayMethod.ALIPAY.type)) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }
        //1.创建订单
        OrderVO orderVO = orderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();

        //2.创建订单以后移除购物车中已结算的商品
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);
        //3.向支付中心发送当前订单，用于保存支付中心的订单
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);    //回调函数，等支付中心订单完成后调用该地址并将订单状态更新为已付款
        merchantOrdersVO.setAmount(1);      //为了方便测试，所以所有的支付金额都统一更改为1分钱

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //这两个参数是慕课网验证的，现在用不了支付中心，无解，只能手动把数据库的订单状态更改
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        /*支付中心无法使用，所以注释掉
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);
        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(paymentUrl, entity, IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员!");
        }
        */
        return IMOOCJSONResult.ok(orderId);
    }

    @ApiOperation("更改订单状态为已付款")
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(@RequestParam String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @ApiOperation("更改订单状态为已付款")
    @PostMapping("/getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }
}