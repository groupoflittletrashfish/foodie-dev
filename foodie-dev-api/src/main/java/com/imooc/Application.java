package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by Administrator on 2020/8/6.
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = "com.imooc.mapper")
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})    //扫描如下包
@EnableScheduling
@EnableRedisHttpSession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
