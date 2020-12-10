package com.imooc.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 拦截器，统计所有服务接口的运行时间，拦截器类型如下：
 * Before:
 * After:
 * Around:方法前后都会执行
 * AfterThrowing:方法内部发生错误时候执行
 * AfterReturning：前置后置环绕都执行完成后执行
 *
 * @program: foodie-dev
 * @description: Service层运行时间统计
 * @author: noname
 * @create: 2020-12-10 18:00
 **/
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

    public Object recordTimeLog(ProceedingJoinPoint joinPoint) {
        return null;
    }


}