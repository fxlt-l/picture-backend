package com.xiaochen.picturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author 小陈
 * @version 1.0
 * @description: 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    USER("普通用户", "user"),
    ADMIN("管理员", "admin");
    private final String text;
    private final String value;

    UserRoleEnum (String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(String value) {
        if(ObjUtil.isEmpty(value))
            return null;
        for (UserRoleEnum roleEnum : UserRoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }

}
