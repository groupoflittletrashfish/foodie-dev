package com.imooc.es.service;

import com.imooc.util.PagedGridResult;

public interface ItemsESService {

    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize);
}
