package com.xiaochen.picturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xiaochen.picturebackend.common.ErrorCode;
import com.xiaochen.picturebackend.constant.CommonConstant;
import com.xiaochen.picturebackend.exception.ThrowUtils;
import com.xiaochen.picturebackend.mapper.UserMapper;
import com.xiaochen.picturebackend.model.dto.user.UserQueryRequest;
import com.xiaochen.picturebackend.model.entity.User;
import com.xiaochen.picturebackend.model.enums.UserRoleEnum;
import com.xiaochen.picturebackend.model.vo.LoginUserVO;
import com.xiaochen.picturebackend.model.vo.UserVO;
import com.xiaochen.picturebackend.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaochen.picturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 小陈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-12-21 18:13:34
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码不一致");
        ThrowUtils.throwIf(userAccount.length() < 4 || userAccount.length() > 20, ErrorCode.PARAMS_ERROR, "账号长度不合法");
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 20, ErrorCode.PARAMS_ERROR, "密码长度不合法");
        //2.检查是否重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");
        //3.加密
        userPassword = getEncryptPassword(userPassword);
        //4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        boolean saveUser = this.save(user);
        ThrowUtils.throwIf(!saveUser, ErrorCode.PARAMS_ERROR, "用户注册失败");
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4 || userAccount.length() > 20, ErrorCode.PARAMS_ERROR, "账号长度不合法");
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 20, ErrorCode.PARAMS_ERROR, "密码长度不合法");
        //2.加密
        userPassword = getEncryptPassword(userPassword);
        //3.查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, userPassword);
        User user = this.getOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 用户查询
     */
    @Override
    public LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), User::getId, id)
                    .like(StrUtil.isNotBlank(userName), User::getUserName, userName)
                    .like(StrUtil.isNotBlank(userAccount), User::getUserAccount, userAccount)
                    .like(StrUtil.isNotBlank(userProfile), User::getUserProfile, userProfile)
                    .eq(StrUtil.isNotBlank(userRole), User::getUserRole, userRole);
        // 排序白名单
        if (StrUtil.equalsAny(sortField, "id", "userName", "createTime","userAccount",  "userRole")) {
            SFunction<User, ?> col = switch (sortField) {
                case "userName" -> User::getUserName;
                case "createTime" -> User::getCreateTime;
                case "userAccount" -> User::getUserAccount;
                case "userRole" -> User::getUserRole;
                default -> User::getId;
            };
            if(StrUtil.equals(sortOrder, CommonConstant.SORT_ORDER_ASC)) {
                queryWrapper.orderByAsc(col);
                } else {
                queryWrapper.orderByDesc(col);
            }
        }
        return queryWrapper;
    }


    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObject == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        return user;
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user == null) {
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 判断用户是否为管理员
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && StrUtil.equals(user.getUserRole(), UserRoleEnum.ADMIN.getValue());
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollectionUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(user -> getUserVO(user)).collect(Collectors.toList());
    }

    @Override
    public LoginUserVO getLoginUserVO(User user){
        return BeanUtil.copyProperties(user, LoginUserVO.class);
    }
    @Override
    public String getEncryptPassword(String userPassword){
        //加盐
        final String SALT = "xiaochen";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}




