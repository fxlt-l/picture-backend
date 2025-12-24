package com.xiaochen.picturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 小陈
 * @version 1.0
 * @description: 登录请求类
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;

}
