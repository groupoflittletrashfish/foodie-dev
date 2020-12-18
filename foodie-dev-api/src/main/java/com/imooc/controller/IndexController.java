package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.enums.YesOrNo;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@RequestMapping("/index")
@ApiIgnore
@Slf4j
@Api(value = "首页", tags = {"首页展示的相关接口"})
public class IndexController {

    @Resource
    private CarouselService carouselService;
    @Resource
    private CategoryService categoryService;

    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        return IMOOCJSONResult.ok(carouselService.queryAll(YesOrNo.YES.type));
    }


    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        return IMOOCJSONResult.ok(categoryService.queryAllRootLevelCat());
    }

    @ApiOperation("获取菜单子分类")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(@PathVariable Integer rootCatId) {
        if (Objects.isNull(rootCatId)) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        return IMOOCJSONResult.ok(categoryService.getSubCatList(rootCatId));
    }


    @ApiOperation("查询每个一级分类下的最新6个商品")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(@PathVariable Integer rootCatId) {
        if (Objects.isNull(rootCatId)) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        return IMOOCJSONResult.ok(categoryService.getSixNewItemsLazy(rootCatId));
    }
}
