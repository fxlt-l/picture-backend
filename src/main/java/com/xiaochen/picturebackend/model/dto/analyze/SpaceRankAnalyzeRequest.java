package com.xiaochen.picturebackend.model.dto.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}

