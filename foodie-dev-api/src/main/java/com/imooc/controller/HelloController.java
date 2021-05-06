package com.imooc.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2021-01-05 13:46
 **/
@RestController
@ApiIgnore
@Slf4j
@Api(value = "测试用", tags = {"测试用"})
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }


    @GetMapping("setSession")
    public Object setSession(HttpServletRequest request) {

        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        return "ok";
    }
}