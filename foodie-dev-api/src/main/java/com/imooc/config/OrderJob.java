package com.imooc.config;

import com.imooc.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-24 11:11
 **/
@Component
public class OrderJob {

    @Resource
    private OrderService orderService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoCloseOrder() {
        orderService.closeOrder();
    }
}