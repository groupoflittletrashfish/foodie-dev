package com.imooc.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: foodie-dev
 * @description: 用于展示商品评价的VO
 * @author: noname
 * @create: 2020-12-15 17:17
 **/
@Data
public class ItemCommentVO implements Serializable {

    private Integer commentLevel;
    private String content;
    private String sepcName;
    private Date createdTime;
    private String userFace;
    private String nickname;
}