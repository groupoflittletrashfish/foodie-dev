package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.util.CookieUtils;
import com.imooc.util.JsonUtils;
import com.imooc.util.MD5Utils;
import com.imooc.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2020/8/14.
 * <p>
 * 登录用Controller
 */
@RestController
@RequestMapping("passport")
@Api(value = "注册登录", tags = {"用于注册登录相关的接口"})
public class PassportController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private RedisOperator redisOperator;

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
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        return IMOOCJSONResult.ok(isExist);
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
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        if (userService.queryUsernameIsExist(userBO.getUsername())) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        if (userBO.getPassword().length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }
        if (!StringUtils.equals(userBO.getPassword(), userBO.getConfirmPassword())) {
            return IMOOCJSONResult.errorMsg("两次密码不一致");
        }
        Users userResult = userService.createUser(userBO);
//        userResult = setNull(userResult);

        //实现用户的redis会话
        UsersVO usersVO = conventUsersVO(userResult);

        //设置Cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        //同步购物车数据
        synchShopcartData(userResult.getId(), request, response);
        return IMOOCJSONResult.ok(userResult);
    }




    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (Objects.isNull(userResult)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }
        userResult = setNull(userResult);

        //实现用户的redis会话
        UsersVO usersVO = conventUsersVO(userResult);

        //设置Cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        //同步redis和cookie中的购物车数据
        synchShopcartData(userResult.getId(), request, response);

        return IMOOCJSONResult.ok(userResult);
    }


    @ApiOperation(value = "用户退出登录", notes = "用户退出登录 ", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {
        //清除用户信息
        CookieUtils.deleteCookie(request, response, "user");
        //用户退出登录，清除redis中的用户信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        //清除cookie购物车信息
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return IMOOCJSONResult.ok();
    }


    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        /**
         * redis中无数据，如果cookie中的购物车数据为空，那么这个时候不做任务处理
         *               如果cookie中的购物车不为空，此时直接放入redis中
         * redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖到本地cookie
         *               如果cookie中的购物车不为空，如果cookie中的某个商品在redis中已存在，把cookie中的商品直接覆盖redis中（参考京东）
         * 同步到redis中去了以后，覆盖本地cookie购物车的数据，保本本地购物车的数据是同步的
         */

        //从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);
        if (StringUtils.isBlank(shopcartJsonRedis)) {
            //redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopCartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopCartBO.class);
                List<ShopCartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopCartBO.class);

                // 定义一个待删除list
                List<ShopCartBO> pendingDeleteList = new ArrayList<>();

                for (ShopCartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopCartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
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
