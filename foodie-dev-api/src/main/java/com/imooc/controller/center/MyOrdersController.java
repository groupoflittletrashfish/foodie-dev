package com.imooc.controller.center;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.controller.BaseController;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.util.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }


    //商家发货没有后端，所以这个接口仅用于模拟
    @ApiOperation("商家发货")
    @GetMapping("/deliver")
    public IMOOCJSONResult deliver(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("订单ID不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("用户确认收货")
    @PostMapping("/confirmReceive")
    public IMOOCJSONResult confirmReceive(@RequestParam String orderId, @RequestParam String userId) {
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return IMOOCJSONResult.errorMsg("订单确认收货失败!");
        }
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("用户删除订单")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String orderId, @RequestParam String userId) {
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if (!res) {
            return IMOOCJSONResult.errorMsg("订单删除失败!");
        }
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("获得订单状态数概况")
    @PostMapping("/statusCounts")
    public IMOOCJSONResult statusCounts(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        OrderStatusCountsVO result = myOrdersService.getOrderStatusCounts(userId);
        return IMOOCJSONResult.ok(result);
    }


    @ApiOperation("查询订单动向")
    @PostMapping("/trend")
    public IMOOCJSONResult trend(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult grid = myOrdersService.getOrdersTrend(userId, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }
}