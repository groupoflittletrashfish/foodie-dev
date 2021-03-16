package com.imooc.controller.center;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentService;
import com.imooc.util.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-25 14:06
 **/

@RestController
@RequestMapping("/mycomments")
@Slf4j
@Api(value = "用户中心评价模块", tags = {"用户中心评价模块"})
public class MyCommentController extends BaseController {

    @Resource
    private MyCommentService myCommentService;


    @ApiOperation("查询订单列表")
    @PostMapping("/pending")
    public IMOOCJSONResult query(@RequestParam String userId, @RequestParam String orderId) {
        //判断用户和订单是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        Orders myOrder = (Orders) checkResult.getData();
        //判断该笔订单是否已经评价，评价过了就不要再继续
        if (myOrder.getIsComment() == YesOrNo.YES.type) {
            return IMOOCJSONResult.errorMsg("该笔订单已经评价");
        }
        List<OrderItems> list = myCommentService.queryPendingComment(orderId);
        return IMOOCJSONResult.ok(list);
    }


    @ApiOperation("保存评论列表")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(@RequestParam String userId, @RequestParam String orderId, @RequestBody List<OrderItemsCommentBO> commentList) {
        //判断用户和订单是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        //判断评论内容list不能为空
        if (CollectionUtils.isEmpty(commentList)) {
            return IMOOCJSONResult.errorMsg("评论内容不能为空");
        }
        myCommentService.saveComments(orderId, userId, commentList);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("查询我的评价")
    @PostMapping("/query")
    public IMOOCJSONResult query(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult grid = myCommentService.queryMyComments(userId, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }
}