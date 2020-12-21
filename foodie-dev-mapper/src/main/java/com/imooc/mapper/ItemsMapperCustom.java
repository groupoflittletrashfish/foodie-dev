package com.imooc.mapper;

import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {

    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> paramsMap);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> paramsMap);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> paramsMap);

    public List<ShopCartVO> queryItemsBySpecIds(@Param("paramsList") List<String> specIds);

    public int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);
}