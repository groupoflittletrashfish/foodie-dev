package com.imooc.controller;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.util.JsonUtils;
import com.imooc.util.MD5Utils;
import com.imooc.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2021-01-05 13:46
 **/

@Controller
public class SSOController {

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    private static final String REDIS_USER_TICKET = "redis_user_ticket";
    private static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    private static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @Resource
    private UserService userService;
    @Resource
    private RedisOperator redisOperator;

    @GetMapping("/login")
    public String hello(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);

        //获取全局门票，如果cookie中能够获取到，证明用户登陆过，此时签发一个一次性的临时票据然后回跳
        String userTicket = getCookie(request, REDIS_USER_TICKET);
        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        //用户从未登录过，第一次进入则跳转到CAS的统一登录界面
        return "login";
    }


    /**
     * 验证CAS全局用户门票
     *
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {
        //验证CAS全局门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }
        //验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        //验证门票对应的User会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }


    /**
     * CAS统一登录接口
     * <p>
     * 1.登录后创建用户的全局会话 token
     * 2.创建用户全局门票，用于表示在CAS端是否登录   userTicket
     * 3.创建用户的临时票据，用于回跳回传
     *
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String returnUrl,
                          Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (Objects.isNull(userResult)) {
            model.addAttribute("errmsg", "用户名或密码不正确");
            return "login";
        }

        //对应时序图第8步，实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO));


        //对应时序图第9步，生成ticket门票，全局门票，代表用户在CAS登录过
        String userTicket = UUID.randomUUID().toString().trim();
        //用户全局门票需要放入到CAS端的cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);
        //userticket关联用户id,并且放入redis中，代表这个用户有门票了，可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());
        //对应时序图第10步，生成临时票据，回跳到调用端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();
        //重定向到returnUtl的地址，并且带上一次性的临时票据
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }


    /**
     * 验证临时票据是否有效
     *
     * @param tmpTicket
     * @return
     */
    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        //如果临时票据ok，则需要销毁，并且拿到CAS端cookie中的全局门票，一次再获得用户会话
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        } else {
            //销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        /*
          验证并且获取用户的全局门票
          需要结合登录接口来看
          之前的临时票据只是用于和redis中的临时票据做验证，因为是一次性的，所以在验证通过之后即销毁，就算不销毁也会自动过期
          当临时票据的验证通过以后，则需要从Cookie中读取全局门票，通过全局门票反向获取redis中的用户ID（在登录的时候会向Cookie中写入
          全局门票，并且在redis写入全局门票-用户ID的键值对）
          验证是否存在用户ID，这个没什么好说的
          然后需要验证门票对应的会话是否存在，也就是token,token之前是存入了redis和cookie中，存入的方式是固定前缀+用户ID
         */

        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        //验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }


    @PostMapping("logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        //获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        //清除userTicket票据，redis/cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);
        //清除用户全局会话
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return IMOOCJSONResult.ok();
    }


    private String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }
        String cookieValue = null;
        for (int i = 0; i < cookieList.length; i++) {
            if (cookieList[i].getName().equals(key)) {
                cookieValue = cookieList[i].getValue();
                break;
            }
        }
        return cookieValue;
    }


    private void deleteCookie(String key, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }


    /**
     * 创建临时票据
     *
     * @return
     */
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }


    private void setCookie(String key, String val, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}