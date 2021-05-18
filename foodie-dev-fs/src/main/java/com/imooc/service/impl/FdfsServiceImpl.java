package com.imooc.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.service.FdfsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/17 15:12
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class FdfsServiceImpl implements FdfsService {

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        //fileExtName是指文件的后缀名
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        //获取上传之后的路径
        String path = storePath.getFullPath();
        return path;
    }
}
