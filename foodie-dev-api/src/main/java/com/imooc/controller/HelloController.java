package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@ApiIgnore      //swagger忽略注解，被修饰的controller将不会显示在文档上
@Slf4j
public class HelloController {

    @Resource
    private HelloService helloService;

    @GetMapping("hello")
    public String hello() {
        log.info("日志测试...");
        return "hello";
    }


    @GetMapping("getStudent")
    public Stu getStudent(@RequestParam String id) {
        return helloService.getStudent(id);
    }
}
