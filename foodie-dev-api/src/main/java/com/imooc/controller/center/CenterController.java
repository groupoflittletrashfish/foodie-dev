package com.imooc.controller.center;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.service.center.CenterUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/center")
@Slf4j
@Api(value = "用户中心", tags = {"用户中心展示的相关接口"})
public class CenterController {

    @Resource
    private CenterUserService centerUserService;

    @GetMapping("userInfo")
    @ApiOperation("获取用户信息")
    public IMOOCJSONResult userInfo(@RequestParam String userId) {
        return IMOOCJSONResult.ok(centerUserService.queryUserInfo(userId));
    }
}