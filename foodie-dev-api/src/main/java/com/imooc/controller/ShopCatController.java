package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.util.JsonUtils;
import com.imooc.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@RequestMapping("/shopcart")
@ApiIgnore
@Slf4j
@Api(value = "购物车接口API", tags = {"购物车接口API"})
public class ShopCatController extends BaseController {

    @Resource
    private RedisOperator redisOperator;


    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId, @RequestBody ShopCartBO shopCartBO,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        //前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis
        //需要判断当前购物车中已经包含的商品，如果存在则累加购买数量
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> shopCartList = null;
        if (StringUtils.isNotBlank(shopcartJson)) {
            shopCartList = JsonUtils.jsonToList(shopcartJson, ShopCartBO.class);
            //判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopCartBO sc : shopCartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopCartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopCartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopCartList.add(shopCartBO);
            }
        } else {
            // redis中没有购物车
            shopCartList = new ArrayList<>();
            // 直接添加到购物车中
            shopCartList.add(shopCartBO);
        }
        // 覆盖现有redis中的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartList));
        return IMOOCJSONResult.ok();
    }


    @ApiOperation("从购物车中删除商品")
    @PostMapping("/del")
    public IMOOCJSONResult del(@RequestParam String userId, @RequestParam String itemSpecId,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        //用户在页面删除购物车中的商品数据，如果此时用户已登录，则需要同步删除后端redis购物车中的商品
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopcartJson)) {
            // redis中已经有购物车了
            List<ShopCartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话则删除
            for (ShopCartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopcartList.remove(sc);
                    break;
                }
            }
            // 覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        }

        return IMOOCJSONResult.ok();
    }
}
