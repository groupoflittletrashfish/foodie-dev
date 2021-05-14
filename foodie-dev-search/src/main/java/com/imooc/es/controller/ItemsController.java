package com.imooc.es.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.es.service.ItemsESService;
import com.imooc.util.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/10 17:04
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@RequestMapping("items")
public class ItemsController {

    @Resource
    private ItemsESService itemsESService;

    @GetMapping("/es/search")
    public IMOOCJSONResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page--;

        PagedGridResult grid = itemsESService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return IMOOCJSONResult.ok(grid);
    }
}
