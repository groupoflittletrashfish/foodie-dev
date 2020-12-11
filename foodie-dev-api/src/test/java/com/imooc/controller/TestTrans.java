package com.imooc.controller;

import com.imooc.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2020/8/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestTrans {

    @Resource
    private com.imooc.service.impl.TestTrans testTrans;

    @Test
    public void testRequired() throws Exception {
        testTrans.testRequired();
    }


    @Test
    public void testSupports() throws Exception {
        testTrans.testSupports();
    }


    @Test
    public void testMandatory(){
        testTrans.testMandatory();
    }


    @Test
    public void testNew(){
        testTrans.testNew();
    }


    @Test
    public void testNotSupported(){
        testTrans.testNotSupported();
    }


    @Test
    public void testNever(){
        testTrans.testNever();
    }

    @Test
    public void testNested(){
        testTrans.testNested();
    }
}
