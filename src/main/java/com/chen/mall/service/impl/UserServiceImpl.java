package com.chen.mall.service.impl;

import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.model.pojo.User;
import com.chen.mall.model.mapper.UserMapper;
import com.chen.mall.service.UserService;
import com.chen.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser() {
        // 根据主键查询对象
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) {
        // 查询用户名是否存在，不允许重名
        User result = userMapper.selectByName(userName);
        if (result != null){
            throw new BusinessException(ExceptionEnum.NAME_EXISTED);
        }

        User user = new User();
        user.setUsername(userName);
        try {
            user.setPassword(MD5Utils.getMD5(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int i = userMapper.insertSelective(user);
        if (i == 0){
            throw new BusinessException(ExceptionEnum.INSERT_FAILED);
        }

    }

    @Override
    public User login(String userName, String password) {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User result = userMapper.selectUserNameAndPassword(userName,md5Password);
        if (result == null){
            throw new BusinessException(ExceptionEnum.USERNAME_PASSWORD_FAILED);
        }
        return result;
    }

    @Override
    public void update(User user) {
        int i = userMapper.updateByPrimaryKeySelective(user);
        if (i > 1){
            throw new BusinessException(ExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user){
        // 1是普通用户，2是管理员
        return user.getRole().equals(2);
    }


}
