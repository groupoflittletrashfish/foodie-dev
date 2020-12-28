package com.imooc.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-28 11:12
 **/
@PropertySource("classpath:file-upload-dev.properties")
@ConfigurationProperties(prefix = "file")
@Component
@Data
public class FileUpload {

    private String imageUserFaceLocation;
    private String imageServerUrl;
}