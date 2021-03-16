package com.imooc.controller;

import com.imooc.util.RedisOperator;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2021-01-05 13:46
 **/
@RestController
@RequestMapping("/redis")
@ApiIgnore
@Api(value = "测试用", tags = {"测试用"})
@Slf4j
public class RedisController {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisOperator operator;

    @GetMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        operator.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return operator.get(key);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String key) {
        operator.del(key);
        return "OK";
    }
}