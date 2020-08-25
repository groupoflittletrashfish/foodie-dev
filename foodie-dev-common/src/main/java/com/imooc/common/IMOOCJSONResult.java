package com.imooc.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @program: foodie-dev
 * @description: 通用返回对象
 * @author: gongj
 * @Description: TODO
 * @create: 2020-08-14 17:13
 **/

@Data
@Builder
public class IMOOCJSONResult {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Integer status;

    private String msg;

    private Object data;
    @JsonIgnore
    private String ok;

    public static IMOOCJSONResult errorMsg(String msg, Integer status) {
        return IMOOCJSONResult.builder().msg(msg).status(status).build();
    }

    public static IMOOCJSONResult error(String msg) {
        return errorMsg(msg, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static IMOOCJSONResult ok() {
        return IMOOCJSONResult.builder().status(HttpStatus.OK.value()).build();
    }


    public static IMOOCJSONResult of(Object data) {
        return IMOOCJSONResult.builder().status(HttpStatus.OK.value()).data(data).build();
    }
}