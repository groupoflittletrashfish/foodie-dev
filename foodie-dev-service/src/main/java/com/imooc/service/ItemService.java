package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ShopCartVO;
import com.imooc.util.PagedGridResult;

import java.util.List;

/**
 * Created by Administrator on 2020/12/11.
 */
public interface ItemService {

    public Items queryItemById(String itemId);

    public List<ItemsImg> queryItemImgList(String itemId);

    public List<ItemsSpec> queryItemSpecList(String itemId);

    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品查询商品的评价等级数量
     *
     * @param itemId
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品ID查询商品评价（分页）
     *
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagesComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     *
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);


    /**
     * 根据商品分类ID搜索商品
     *
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);


    /**
     * 根据规格ids查询最新的购物车中的商品数据（用于刷新渲染购物车）
     *
     * @param specIds
     * @return
     */
    public List<ShopCartVO> queryItemsBySpecIds(String specIds);
}