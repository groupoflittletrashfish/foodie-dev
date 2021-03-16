package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@RequestMapping("/items")
@ApiIgnore
@Slf4j
@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
public class ItemsController extends BaseController {

    @Resource
    private ItemService itemService;


    @ApiOperation("查询商品详情")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(@PathVariable String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemsParam);
        return IMOOCJSONResult.ok(itemInfoVO);
    }


    @ApiOperation("查询商品评价等级")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentLevel(@RequestParam String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        return IMOOCJSONResult.ok(itemService.queryCommentCounts(itemId));
    }


    @ApiOperation("查询商品评论")
    @GetMapping("/comments")
    public IMOOCJSONResult comments(@RequestParam String itemId, @RequestParam Integer level,
                                    @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        return IMOOCJSONResult.ok(itemService.queryPagesComments(itemId, level, page, pageSize));
    }


    @ApiOperation("搜索商品列表")
    @GetMapping("/search")
    public IMOOCJSONResult comments(@RequestParam String keywords, @RequestParam String sort,
                                    @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        return IMOOCJSONResult.ok(itemService.searchItems(keywords, sort, page, pageSize));
    }


    @ApiOperation("通过分类ID搜索商品列表")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(@RequestParam Integer catId, @RequestParam String sort,
                                    @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (catId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        return IMOOCJSONResult.ok(itemService.searchItems(catId, sort, page, pageSize));
    }


    @ApiOperation("根据商品规格ids查找最新的商品，用于用户长时间未登录网站，刷新最新的商品数据")
    @GetMapping("/refresh")
    public IMOOCJSONResult refresh(@RequestParam String itemSpecIds) {
        if (StringUtils.isBlank(itemSpecIds)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        return IMOOCJSONResult.ok(itemService.queryItemsBySpecIds(itemSpecIds));
    }


}
