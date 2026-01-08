package com.xiaochen.picturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 小陈
 * @version 1.0
 * @description: 颜色搜图请求封装类
 */
@Data
public class SearchPictureByColorRequest implements Serializable {
    private String picColor;
    private Long spaceId;
    private static final long serialVersionUID = 1L;
}
