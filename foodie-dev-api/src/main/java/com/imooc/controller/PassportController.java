package com.imooc.controller;

import com.imooc.bo.UserBO;
import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.util.CookieUtils;
import com.imooc.util.JsonUtils;
import com.imooc.util.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created by Administrator on 2020/8/14.
 * <p>
 * 登录用Controller
 */
@RestController
@RequestMapping("passport")
@Api(value = "注册登录", tags = {"用于注册登录相关的接口"})
public class PassportController {

    @Resource
    private UserService userService;

    /**
     * 检验用户名是否存在
     *
     * @param username 前端用户名
     * @return 状态码
     */
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.error("用户名不能为空");
        }
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.error("用户名已经存在");
        }
        return IMOOCJSONResult.of(isExist);
    }

    /**
     * 注册
     *
     * @param userBO 用户对象
     * @return 是否成功
     */
    @ApiOperation(value = "注册", notes = "注册", httpMethod = "POST")
    @PostMapping("regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(userBO.getUsername()) || StringUtils.isBlank(userBO.getPassword()) || StringUtils.isBlank(userBO.getConfirmPassword())) {
            return IMOOCJSONResult.error("用户名或者密码不能为空");
        }
        if (userService.queryUsernameIsExist(userBO.getUsername())) {
            return IMOOCJSONResult.error("用户名已经存在");
        }
        if (userBO.getPassword().length() < 6) {
            return IMOOCJSONResult.error("密码长度不能小于6");
        }
        if (!StringUtils.equals(userBO.getPassword(), userBO.getConfirmPassword())) {
            return IMOOCJSONResult.error("两次密码不一致");
        }
        Users users = userService.createUser(userBO);
        users = setNull(users);
        //设置Cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);
        return IMOOCJSONResult.of(users);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.error("用户名或密码不能为空");
        }
        Users users = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (Objects.isNull(users)) {
            return IMOOCJSONResult.error("用户名或密码不正确");
        }
        users = setNull(users);
        //设置Cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);
        return IMOOCJSONResult.of(users);
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
