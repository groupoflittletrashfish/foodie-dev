package com.imooc.service.center;

import com.github.pagehelper.PageInfo;
import com.imooc.util.PagedGridResult;

import java.util.List;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-29 16:27
 **/
public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}