package com.imooc.controller.center;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.util.CookieUtils;
import com.imooc.util.DateUtil;
import com.imooc.util.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-25 14:06
 **/

@RestController
@RequestMapping("/userInfo")
@Slf4j
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
public class CenterUserController extends BaseController {

    @Resource
    private CenterUserService centerUserService;
    @Resource
    private FileUpload fileUpload;


    @PostMapping("update")
    @ApiOperation("修改用户信息")
    public IMOOCJSONResult update(@RequestParam("userId") String userId, @Valid @RequestBody CenterUserBO centerUserBO,
                                  HttpServletRequest request, HttpServletResponse response, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return IMOOCJSONResult.errorMap(errorMap);
        }
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        setNull(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
        return IMOOCJSONResult.ok();
    }


    @PostMapping("uploadFace")
    @ApiOperation("用户头像修改")
    public IMOOCJSONResult uploadFace(@RequestParam("userId") String userId, MultipartFile file, HttpServletRequest request,
                                      HttpServletResponse response) {
        //定义头像保存的地址
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //在路径上为每一个用户增加一个userID,用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;
        if (file != null) {
            FileOutputStream fileOutputStream = null;
            try {

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
                    //文件名称重组 覆盖式上传，若需要增量式，则需要额外拼接时间
                    String newFileName = "face-" + userId + "." + suffix;
                    //上传的头像最终保存的位置
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                    //用于提供给web服务的地址
                    uploadPathPrefix += ("/" + newFileName);
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }
        //获取图片服务地址
        String imageServerUrl = fileUpload.getImageServerUrl();
        //由于浏览器可能出现缓存的情况，所以在这里，我们需要加上时间戳来保证更新后的图片可以及时刷新
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
        userResult = setNull(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
        return IMOOCJSONResult.ok();
    }

    private Map<String, String> getErrors(BindingResult result) {
        List<FieldError> errorList = result.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for (FieldError error : errorList) {
            //发生验证错误对应的某个属性
            String errorField = error.getField();
            //验证错误的信息
            String errorMessage = error.getDefaultMessage();
            map.put(errorField, errorMessage);
        }
        return map;
    }


    private Users setNull(Users users) {
        users.setPassword(null);
        users.setMobile(null);
        users.setEmail(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
        users.setBirthday(null);
        return users;
    }
}