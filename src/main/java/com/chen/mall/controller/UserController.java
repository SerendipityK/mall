package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.common.Constant;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.model.pojo.User;
import com.chen.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public User personalPage(){
        return userService.getUser();
    }

    @PostMapping("/register")
    public ApiRestResponse<Void> register(@RequestParam("userName") String userName,@RequestParam("password") String password){
        if (StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_PASSWORD);
        }

        if (password.length() < 8){
            return ApiRestResponse.error(ExceptionEnum.PASSWORD_TO_SHORT);
        }
        userService.register(userName,password);
        return ApiRestResponse.success();
    }

    @PostMapping("/login")
    public ApiRestResponse<User> login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session){
        if (StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_PASSWORD);
        }

        if (password.length() < 8){
            return ApiRestResponse.error(ExceptionEnum.PASSWORD_TO_SHORT);
        }
        User user = userService.login(userName, password);
        // 保存用户信息时，不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.USER,user);
        return ApiRestResponse.success(user);
    }

    @PostMapping("/user/update")
    public ApiRestResponse<Void> updateUserInfo(HttpSession session,@RequestParam("signature") String signature){
        User currentUser = (User) session.getAttribute(Constant.USER);
        if (currentUser == null){
            return ApiRestResponse.error(ExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setPersonalizedSignature(signature);
        user.setId(currentUser.getId());
        userService.update(user);
        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    public ApiRestResponse<Void> logout(HttpSession session){
        session.removeAttribute(Constant.USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/adminLogin")
    public ApiRestResponse<User> adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session){
        if (StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ExceptionEnum.NEED_USER_PASSWORD);
        }

        if (password.length() < 8){
            return ApiRestResponse.error(ExceptionEnum.PASSWORD_TO_SHORT);
        }
        User user = userService.login(userName, password);
        // 校验是否是管理员
        if (userService.checkAdminRole(user)) {
            // 是管理员，执行操作
            // 保存用户信息时，不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.USER,user);
            return ApiRestResponse.success(user);
        }else {
            return ApiRestResponse.error(ExceptionEnum.ADMIN_USER_FAILED);
        }

    }


}
