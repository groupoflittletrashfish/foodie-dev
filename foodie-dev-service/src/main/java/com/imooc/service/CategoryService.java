package com.imooc.service;

import com.imooc.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2020/12/11.
 */
public interface CategoryService {

    /**
     * 查询所有一级分类
     *
     * @return
     */
    public List<Category> queryAllRootLevelCat();
}