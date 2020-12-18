package com.imooc.service.impl;

import com.imooc.pojo.bo.UserBO;
import com.imooc.enums.Sex;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.util.DateUtil;
import com.imooc.util.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by Administrator on 2020/8/14.
 * <p>
 * 用户接口实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Resource
    private UsersMapper usersMapper;
    @Resource
    private Sid sid;

    @Override
    public boolean queryUsernameIsExist(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        int count = usersMapper.selectCountByExample(example);
        return count > 0;
    }

    @Override
    @Transactional
    public Users createUser(UserBO userBO) throws Exception {
        Users user = new Users();
        user.setId(sid.nextShort());
        user.setUsername(userBO.getUsername());
        user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        user.setNickname(userBO.getUsername());
        //设置默认头像
        user.setFace(USER_FACE);
        //设置默认的生日
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        //设置默认性别为保密
        user.setSex(Sex.SECRET.getType());
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        return usersMapper.selectOneByExample(userExample);
    }
}
