package com.imooc.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 从自定义的资源文件中读取配置
 *
 * @author ：liwuming
 * @date ：Created in 2021/5/18 14:03
 * @description：
 * @modified By：
 * @version:
 */
@Component
@PropertySource("classpath:file.properties")
@ConfigurationProperties(prefix = "file")
@Data
public class FileResource {

    private String host;
}
