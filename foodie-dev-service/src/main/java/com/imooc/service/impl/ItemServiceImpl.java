package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.imooc.enums.CommentLevel;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopCartVO;
import com.imooc.service.ItemService;
import com.imooc.util.DesensitizationUtil;
import com.imooc.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: foodie-dev
 * @description: 商品实现类
 * @author: noname
 * @create: 2020-12-15 11:43
 **/

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;


    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemExample = new Example(ItemsImg.class);
        Example.Criteria criteria = itemExample.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsImgMapper.selectByExample(itemExample);
    }

    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemExample = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemExample.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsSpecMapper.selectByExample(itemExample);
    }

    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example itemExample = new Example(ItemsParam.class);
        Example.Criteria criteria = itemExample.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsParamMapper.selectOneByExample(itemExample);
    }

    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);

        CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
        countsVO.setGoodCounts(goodCounts);
        countsVO.setNormalCounts(normalCounts);
        countsVO.setBadCounts(badCounts);
        countsVO.setTotalCounts(goodCounts + normalCounts + badCounts);
        return countsVO;
    }

    @Override
    public PagedGridResult queryPagesComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("itemId", itemId);
        map.put("level", level);

        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        for (ItemCommentVO itemCommentVO : list) {
            itemCommentVO.setNickname(DesensitizationUtil.commonDisplay(itemCommentVO.getNickname()));
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("keywords", keywords);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("catId", catId);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);
        return setterPagedGrid(list, page);
    }

    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);
        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseItemSpecStock(String specId, int buyCounts) {
        //使用synchronized 不推荐使用，集群下无用，性能低下
        //数据库锁：不推荐，导致数据库性能低下
        //分布式锁：zookeeper/redis
        //lockUtil.getLock();   --加锁

        //lockUtil.unLock();    --解锁
        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1) {
            throw new RuntimeException("订单创建失败,库存不足");
        }
    }

    private Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (level != null) {
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }


    private PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}