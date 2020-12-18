package com.imooc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2020/8/6.
 */
@RestController
@ApiIgnore      //swagger忽略注解，被修饰的controller将不会显示在文档上
@Slf4j
public class BaseController {

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;
}
