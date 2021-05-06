package com.imooc.controller.intercepter;

import com.imooc.common.IMOOCJSONResult;
import com.imooc.util.JsonUtils;
import com.imooc.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ：liwuming
 * @date ：Created in 2021/4/9 13:56
 * @description：
 * @modified By：
 * @version:
 */
public class UserTokenIntercepter implements HandlerInterceptor {

    @Resource
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 在访问controller调用之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入到拦截器，被拦截...");
        String userToken = request.getHeader("headerUserToken");
        String userId = request.getHeader("headerUserId");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)) {
                returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    returnErrorResponse(response, IMOOCJSONResult.errorMsg("账号在异地登录..."));
                    return false;
                }
            }
        } else {
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }

        return true;
    }


    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result) {
        OutputStream out = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        try {
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求访问controller之后，渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问到controller之后，渲染视图之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
