package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @program: foodie-dev
 * @description: 跨域配置
 * @author: noname
 * @create: 2020-08-24 10:48
 **/
@Configuration
public class CoreConfig {

    public CoreConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //设置哪些地址允许跨域
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://192.168.222.145:8080");
        config.addAllowedOrigin("http://192.168.222.145");
        config.addAllowedOrigin("*");
        //设置是否允许cookie信息
        config.setAllowCredentials(true);
        //设置允许请求的方式
        config.addAllowedMethod("*");
        //设置允许的Header
        config.addAllowedHeader("*");

        //为URL添加映射的路径
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //表示该配置适用于所有的路由
        corsConfigurationSource.registerCorsConfiguration("/**", config);

        return new CorsFilter(corsConfigurationSource);
    }
}