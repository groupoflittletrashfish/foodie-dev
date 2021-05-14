package com.imooc.es.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 该包对于索引的创建/删除等操作并不是那么支持，一般来讲也不会用代码去控制索引的创建和删除。即使在下方指定了副本数和分片数，实际上运行的时候并不生效
 *
 * @author ：liwuming
 * @date ：Created in 2021/5/6 17:30
 * @description：
 * @modified By：
 * @version:
 */

@Data
@Document(indexName = "stu", type = "_doc", shards = 3, replicas = 3)
public class Stu {

    /**
     * Id注解,是将该字段直接作为ES本身的主键，如果不设置，ES将会自动生成一个ID
     */
    @Id
    private Long stuId;
    @Field(store = true)
    private String name;
    @Field(store = true)
    private Integer age;
    /**
     * 设置字段的类型，注意：此处即使设置了keyword类型，但是实际上也会是text类型，这个是ES本身的处理
     */
    @Field(store = true, type = FieldType.Keyword)
    private String sign;
    @Field(store = true)
    private String description;
    @Field(store = true)
    private Float money;

}
