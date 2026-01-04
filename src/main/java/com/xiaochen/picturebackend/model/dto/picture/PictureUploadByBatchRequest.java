package com.xiaochen.picturebackend.model.dto.picture;

import lombok.Data;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
public class PictureUploadByBatchRequest {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;
    /**
     * 名称前缀
     */
    private String namePrefix;
    /**
     * 空间 id
     */
    private Long spaceId;


}

