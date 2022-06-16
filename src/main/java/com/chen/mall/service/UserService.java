package com.chen.mall.service;

import com.chen.mall.model.pojo.User;

public interface UserService {
    User getUser();

    void register(String userName,String password);

    User login(String userName,String password);

    void update(User user);

    boolean checkAdminRole(User user);
}
