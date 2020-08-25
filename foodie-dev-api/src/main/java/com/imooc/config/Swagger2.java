package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @program: foodie-dev
 * @description: Swagger配置
 * @author: noname
 * @create: 2020-08-18 16:38
 **/

@Configuration
@EnableSwagger2     //开启swagger2
public class Swagger2 {

    /*配置swagger2核心配置
    *
    * DocumentationType.SWAGGER_2 :指定文档的类型为DocumentationType.SWAGGER_2，目前最新的版本
    * select().apis(RequestHandlerSelectors.basePackage("com.imooc.controller") : 表示需要扫描的controller所在的路径
    *
    * */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().
                apis(RequestHandlerSelectors.basePackage("com.imooc.controller")).paths(PathSelectors.any()
        ).build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("天天吃货 电商平台接口api").contact(new Contact("作者", "http://www.imooc.com",
                "联系人邮箱824160554@qq.com")).description("专为天天吃货提供的api文档").version("1.0.1").termsOfServiceUrl("https://网站地址").build();
    }
}