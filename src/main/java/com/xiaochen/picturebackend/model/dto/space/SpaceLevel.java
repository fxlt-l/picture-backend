package com.xiaochen.picturebackend.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}

