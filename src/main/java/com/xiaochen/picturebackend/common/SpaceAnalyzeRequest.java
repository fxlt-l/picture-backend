package com.xiaochen.picturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}

