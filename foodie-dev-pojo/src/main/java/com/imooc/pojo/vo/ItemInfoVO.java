package com.imooc.pojo.vo;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: foodie-dev
 * @description: 六个最新商品的简单数据类型
 * @author: noname
 * @create: 2020-12-14 18:05
 **/
@Data
public class ItemInfoVO implements Serializable {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}