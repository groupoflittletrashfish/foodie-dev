package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.bo.ShopCartBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@RequestMapping("/shopcart")
@ApiIgnore
@Slf4j
@Api(value = "购物车接口API", tags = {"购物车接口API"})
public class ShopCatController extends BaseController {


    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId, @RequestBody ShopCartBO shopCartBO,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        //TODO REDIS部分完善

        return IMOOCJSONResult.ok();
    }


    @ApiOperation("从购物车中删除商品")
    @PostMapping("/del")
    public IMOOCJSONResult del(@RequestParam String userId, @RequestParam String itemSpecId,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        //TODO REDIS部分完善

        return IMOOCJSONResult.ok();
    }
}
