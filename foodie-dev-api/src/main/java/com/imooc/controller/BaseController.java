package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@ApiIgnore      //swagger忽略注解，被修饰的controller将不会显示在文档上
@Slf4j
public class BaseController {
    @Resource
    public MyOrdersService myOrdersService;

    public static final Integer COMMON_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    public static final String FOODIE_SHOPCART = "shopcart";

    /**
     * 支付中心的地址URL，不过要收费，所以用不了，只能手动去更改数据库里的订单状态
     */
    String paymentUrl = "";

    /**
     * 支付回调接口，因为支付中心无法使用，所以配置了也没有意义。但需要注意的是：地址需要为外网地址，或者使用内网穿透
     */
    String payReturnUrl = "http://localhost:8088/orders/notifyMerchantOrderPaid";

    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" + File.separator + "images" + File.separator + "foodie" + File.separator + "faces";


    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(order);
    }
}
