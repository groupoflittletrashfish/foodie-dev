package com.imooc.es.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/6 17:30
 * @description：
 * @modified By：
 * @version:
 */

@Data
@Document(indexName = "stu", type = "_doc")
public class Stu {

    @Id
    private Long stuId;
    @Field(store = true)
    private String name;
    @Field(store = true)
    private Integer age;
}
