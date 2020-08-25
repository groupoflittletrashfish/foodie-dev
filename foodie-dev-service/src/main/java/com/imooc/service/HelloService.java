package com.imooc.service;

import com.imooc.pojo.Stu;

/**
 * Created by Administrator on 2020/8/11.
 */
public interface HelloService {

    Stu getStudent(String id);

    void saveStu();

    int saveParent();

    int saveChild();
}
