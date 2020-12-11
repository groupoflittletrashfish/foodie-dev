package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.HelloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2020/8/11.
 */

@Service
public class HelloServiceImpl implements HelloService {

    @Resource
    private StuMapper stuMapper;

    @Override
    public Stu getStudent(String id) {
        return stuMapper.selectByPrimaryKey(1);
    }

    @Override
    public void saveStu() {

    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public int saveParent() {
        Stu stu = new Stu();
        stu.setId(1);
        stu.setName("parent");
        stu.setAge(10);
        int i = 1/0;
        return stuMapper.insert(stu);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int saveChild() {
        Stu stu = new Stu();
        stu.setId(2);
        stu.setName("children");
        stu.setAge(20);
        return stuMapper.insert(stu);
    }
}
