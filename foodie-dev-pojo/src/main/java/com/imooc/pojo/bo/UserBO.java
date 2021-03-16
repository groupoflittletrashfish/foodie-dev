package com.imooc.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @program: foodie-dev
 * @description: 用户对象
 * @author: noname
 * @create: 2020-08-14 17:58
 **/
@Data
@ApiModel(value = "用户对象BO", description = "从客户端，由用户端传入的数据封装")
public class UserBO {

    @ApiModelProperty(value = "用户名", example = "noname", required = true)
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("确认密码")
    private String confirmPassword;
}