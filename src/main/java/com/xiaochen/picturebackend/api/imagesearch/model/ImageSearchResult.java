package com.xiaochen.picturebackend.api.imagesearch.model;

import lombok.Data;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}

