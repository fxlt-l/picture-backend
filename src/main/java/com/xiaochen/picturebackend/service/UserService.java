package com.xiaochen.picturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaochen.picturebackend.model.dto.user.UserQueryRequest;
import com.xiaochen.picturebackend.model.entity.User;
import com.xiaochen.picturebackend.model.vo.LoginUserVO;
import com.xiaochen.picturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 小陈
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-12-21 18:13:34
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    String getEncryptPassword(String userPassword);

    User getLoginUser(HttpServletRequest request);

    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);
}
