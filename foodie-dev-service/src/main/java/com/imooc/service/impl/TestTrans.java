package com.imooc.service.impl;

import com.imooc.service.HelloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2020/8/13.
 *
 * 在同一个service中，只要入口方法有事务，那么则子方法的事务都将失效。所以在此类中测试
 *
 */
@Service
public class TestTrans {

    @Resource
    private HelloService helloService;

    /**
     * REQUIRED：必须运行在事务中，有则加入，无则创建。
     * 1.入口为REQUIRED，saveParent为New或者NOT_SUPPORT,此时saveParent成功
     *
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testRequired() {
        helloService.saveParent();
        int i = 1 / 0;
        helloService.saveChild();
    }


    /**
     * SUPPORTS:如果有事务则加入事务，如果没事务则以非事务状态运行
     * 1.入口SUPPORTS，由于此时没有事务，则以非事务状态运行。saveParent为REQUIRED，则saveParent内部开启事务，并提交成功
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void testSupports(){
        helloService.saveParent();
        int i = 1 / 0;
        helloService.saveChild();
    }


    /**
     * MANDATORY：需要支持当前事务，若当前没有事务，则抛出异常，所以MANDATORY不适合用在入口方法上。
     */
//    @Transactional(propagation = Propagation.MANDATORY)
    public void testMandatory(){
        helloService.saveParent();
        int i = 1 / 0;
        helloService.saveChild();
    }


    /**
     * NEW: 如果当前已有事务，挂起该事务。并且重新新建一个事务，一般用于子方法中(子事务若异常回滚，父事务也将回滚)
     * 1.入口REQUIRED，saveChild为REQUIRED，此时saveParent中有一个异常，且是NEW，那么整个事务都将回滚。父事务捕捉到了子事务的回滚
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testNew(){
        helloService.saveChild();
        helloService.saveParent();
    }


    /**
     * NOTsUPPORTED: 以非事务方式运行。若当前存在事务，则挂起该事务
     * 1.入口不管是否存在事务，只要子方法是NOTsUPPORTED，则都是独立的，异常也不会回滚
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testNotSupported(){
        helloService.saveChild();
        helloService.saveParent();
        int i = 1 / 0;
    }


    /**
     * NEVER:以非事务方式运行，若当前存在事务，则抛出异常
     * 1.入口方法REQUIRED，saveParent为NEVER，则抛出异常
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testNever(){
        helloService.saveParent();
        helloService.saveChild();
    }


    /**
     * NESTED:若当前无事务，他将会开启事务。若当前有事务，则开启一个新的子事务，与NEW不同，NEW相当于存在两个事务，NESTED则是父子嵌套
     * 事务。所以如果父事务异常,子事务同样会回滚。这是与NEW的区别。反过来，子事务中有异常，父事务同样也会回滚
     * 1. 入口方法REQUIRED，saveParent使用NESTED，当入口方法异常，则saveParent同样回滚
     * 2.入口方法和saveChild都是REQUIRED，saveParent是NESTED且有异常，此时将会整体回滚
     * 3.应用：可以在此方法中的saveParent外侧捕获异常，这个功能被成为临时存档，这样就不会影响其他的方法运行。也就是说父事务可以选择性的根据
     * 子事务的情况选择回滚或者不回滚
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testNested(){
        helloService.saveChild();
        helloService.saveParent();
    }
}
