package com.imooc.controller.center;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.controller.BaseController;
import com.imooc.service.center.MyOrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-25 14:06
 **/

@RestController
@RequestMapping("/myorders")
@Slf4j
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单"})
public class MyOrdersController extends BaseController {

    @Resource
    private MyOrdersService myOrdersService;


    @ApiOperation("查询订单列表")
    @PostMapping("/query")
    public IMOOCJSONResult query(@RequestParam String userId, @RequestParam Integer orderStatus,
                                 @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        return IMOOCJSONResult.ok(myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize));
    }


    //商家发货没有后端，所以这个接口仅用于模拟
    @ApiOperation("商家发货")
    @PostMapping("/deliver")
    public IMOOCJSONResult deliver(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("订单ID不能为空");
        }
        centerOrderService.updateDeliverOrderStatus(orderId);
        return IMOOCJSONResult.ok();
    }
}