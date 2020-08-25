package com.imooc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: foodie-dev
 * @description: 时间处理工具类
 * @author: noname
 * @create: 2020-08-17 11:42
 **/
public class DateUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    public static Date stringToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}