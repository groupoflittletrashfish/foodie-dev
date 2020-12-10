package com.imooc.service;

import com.imooc.bo.UserBO;
import com.imooc.pojo.Users;

/**
 * Created by Administrator on 2020/8/14.
 * <p>
 * 用户接口
 */
public interface UserService {

    boolean queryUsernameIsExist(String username);

    Users createUser(UserBO user) throws Exception;

    public Users queryUserForLogin(String username, String password);
}
