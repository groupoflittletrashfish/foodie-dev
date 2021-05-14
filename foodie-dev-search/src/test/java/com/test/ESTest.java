package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在ES7之前的版本，可以使用spring-boot-starter-data-elasticsearch，因为其没有同步更新到ES7。官方是推荐high-light包，由于是跟着视频走，就先用该依赖
 * 注意netty本身存在一个issue，可以通过ESConfig来解决
 *
 * @author ：liwuming
 * @date ：Created in 2021/5/6 17:27
 * @description：
 * @modified By：
 * @version:
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建索引并插入数据/插入数据，通过对对象进行注解的方式，此例可以查看Stu这个类，各属性上有注解
     * ES没有索引的情况下，执行将会生成索引并插入数据
     * 第二次执行的时候，如果数据的ID是一样的，其他值不一样，则相当于是更新操作
     * <p>
     * 一般来讲并不建议直接使用代码去操作索引的结构，包含创建/修改/删除索引，一般只会操作数据本身
     */
    @Test
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setStuId(1001L);
        stu.setName("noname");
        stu.setAge(180);
        stu.setSign("i am noname");
        stu.setMoney(0f);
        stu.setDescription("没钱不开心");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(indexQuery);
    }


    /**
     * 删除索引
     */
    @Test
    public void deleteIndexStu() {
        elasticsearchTemplate.deleteIndex(Stu.class);
    }


    /**
     * 更新数据
     */
    @Test
    public void updateStuDoc() {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign", "修改后的sign");
        sourceMap.put("money", 100000000);
        sourceMap.put("age", 33);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);
        UpdateQuery updateQuery = new UpdateQueryBuilder().withClass(Stu.class).withId("1001").withIndexRequest(indexRequest).build();
        elasticsearchTemplate.update(updateQuery);
    }

    /**
     * 根据ID删除文档
     */
    @Test
    public void deleteStu() {
        elasticsearchTemplate.delete(Stu.class, "1001");
    }

    /**
     * 根据ID查询文档
     */
    @Test
    public void getStuDoc() {
        GetQuery query = new GetQuery();
        query.setId("1001");
        Stu stu = elasticsearchTemplate.queryForObject(query, Stu.class);
        System.out.println(stu);
    }


    /**
     * 分页查询
     */
    @Test
    public void search() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("description", "不开心"))
                .withPageable(pageable).build();
        //带有分页信息的返回结果
        AggregatedPage<Stu> pagedStu = elasticsearchTemplate.queryForPage(query, Stu.class);
        System.out.println("总分也数目为:" + pagedStu.getTotalPages());
        List<Stu> stuList = pagedStu.getContent();
        for (Stu stu : stuList) {
            System.out.println(stu);
        }
    }

    @Test
    public void highlightStuDoc() {
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(0, 10);

        //排序
        SortBuilder sortBuilder = new FieldSortBuilder("money").order(SortOrder.DESC);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("description", "不开心"))
                //高亮设置
                .withHighlightFields(new HighlightBuilder.Field("description").preTags(preTag).postTags(postTag))
                .withSort(sortBuilder)
                .build();
        //带有分页信息的返回结果
        AggregatedPage<Stu> pagedStu = elasticsearchTemplate.queryForPage(query, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Stu> stuListHighlight = new ArrayList<>();
                SearchHits his = searchResponse.getHits();
                for (SearchHit h : his) {
                    HighlightField highlightField = h.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();

                    Long stuId = (Long) h.getSourceAsMap().get("id");
                    String name = (String) h.getSourceAsMap().get("name");
                    Integer age = (Integer) h.getSourceAsMap().get("age");
                    String sign = (String) h.getSourceAsMap().get("sign");
                    Object money = (Object) h.getSourceAsMap().get("money");

                    Stu stuHl = new Stu();
                    stuHl.setDescription(description);
                    stuHl.setStuId(stuId);
                    stuHl.setName(name);
                    stuHl.setAge(age);
                    stuHl.setSign(sign);
                    stuHl.setMoney(Float.valueOf(money.toString()));
                    stuListHighlight.add(stuHl);
                }
                if (CollectionUtils.isNotEmpty(stuListHighlight)) {
                    return new AggregatedPageImpl<>((List<T>) stuListHighlight);
                }
                return null;
            }
        });
        System.out.println("总分页数目为:" + pagedStu.getTotalPages());
        List<Stu> stuList = pagedStu.getContent();
        for (Stu stu : stuList) {
            System.out.println(stu);
        }
    }
}
