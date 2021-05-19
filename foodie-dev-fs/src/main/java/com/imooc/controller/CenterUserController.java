package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.util.CookieUtils;
import com.imooc.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：liwuming
 * @date ：Created in 2021/5/17 15:41
 * @description：
 * @modified By：
 * @version:
 */

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {

    @Resource
    private FdfsService fdfsService;
    @Resource
    private FileResource fileResource;
    @Resource
    private CenterUserService centerUserService;

    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(@RequestParam("userId") String userId, MultipartFile file, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String path = null;
        if (file != null) {
            //获取文件上传的名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                String[] fileNameArr = fileName.split("\\.");
                //获取文件后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];
                if (!suffix.equalsIgnoreCase("png") && !suffix.equalsIgnoreCase("jpg")
                        && !suffix.equalsIgnoreCase("jpeg")) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确!");
                }

                path = fdfsService.upload(file, suffix);
                System.out.println(path);
            } else {
                return IMOOCJSONResult.errorMsg("文件不能为空");
            }
            if (StringUtils.isNotBlank(path)) {
                String finalUserFaceUrl = fileResource.getHost() + path;
                Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
                UsersVO usersVO = conventUsersVO(userResult);
                CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
            } else {
                return IMOOCJSONResult.errorMsg("上传头像失败");
            }
        }
        return IMOOCJSONResult.ok();
    }
}



