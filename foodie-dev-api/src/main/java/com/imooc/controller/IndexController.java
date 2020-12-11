package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.enums.YesOrNo;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

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
        return IMOOCJSONResult.of(carouselService.queryAll(YesOrNo.YES.type));
    }


    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        return IMOOCJSONResult.of(categoryService.queryAllRootLevelCat());
    }
}
