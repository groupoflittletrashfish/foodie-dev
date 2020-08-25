package com.imooc.util;

import java.lang.reflect.InvocationTargetException;

/**
 * @program: foodie-dev
 * @description: 自定义的BeanUtil工具
 * @author: noname
 * @create: 2020-08-17 11:29
 **/
public class BeanUtils {

    public static void copyProperties(Object dest, Object orign) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orign);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void copyProperty(Object bean, String name, Object value) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperty(bean, name, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}