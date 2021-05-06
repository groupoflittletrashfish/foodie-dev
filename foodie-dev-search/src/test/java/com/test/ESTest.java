package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/6 17:27
 * @description：
 * @modified By：
 * @version:
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

//    @Resource
//    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createIndexStu() {
//        Stu stu = new Stu();
//        stu.setStuId(1001L);
//        stu.setName("noname");
//        stu.setAge(18);
//
//        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
//        elasticsearchTemplate.index(indexQuery);
        System.out.println("111");
    }
}
