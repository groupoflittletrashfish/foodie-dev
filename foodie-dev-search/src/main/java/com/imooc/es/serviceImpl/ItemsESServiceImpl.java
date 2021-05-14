package com.imooc.es.serviceImpl;

import com.imooc.es.pojo.Items;
import com.imooc.es.service.ItemsESService;
import com.imooc.util.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/10 17:49
 * @description：
 * @modified By：
 * @version:
 */

@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(page, pageSize);

        //排序
        SortBuilder sortBuilder = null;

        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
        }

        String itemNameField = "itemName";
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery(itemNameField, keywords))
                //高亮设置
                .withHighlightFields(new HighlightBuilder.Field(itemNameField).preTags(preTag).postTags(postTag))
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();
        //带有分页信息的返回结果
        AggregatedPage<Items> pagedItems = elasticsearchTemplate.queryForPage(query, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Items> itemHighLightList = new ArrayList<>();
                SearchHits his = searchResponse.getHits();
                for (SearchHit h : his) {
                    HighlightField highlightField = h.getHighlightFields().get(itemNameField);
                    String itemName = highlightField.getFragments()[0].toString();

                    String itemId = (String) h.getSourceAsMap().get("itemId");
                    String imgUrl = (String) h.getSourceAsMap().get("imgUrl");
                    Integer price = (Integer) h.getSourceAsMap().get("price");
                    Integer sellCounts = (Integer) h.getSourceAsMap().get("sellCounts");

                    Items item = new Items();
                    item.setItemId(itemId);
                    item.setItemName(itemName);
                    item.setImgUrl(imgUrl);
                    item.setPrice(price);
                    item.setSellCounts(sellCounts);
                    itemHighLightList.add(item);
                }

                return new AggregatedPageImpl<>((List<T>) itemHighLightList, pageable, searchResponse.getHits().getTotalHits());
            }
        });
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(pagedItems.getContent());
        gridResult.setPage(page + 1);
        gridResult.setTotal(pagedItems.getTotalPages());
        gridResult.setRecords(pagedItems.getTotalElements());
        return gridResult;
    }
}
