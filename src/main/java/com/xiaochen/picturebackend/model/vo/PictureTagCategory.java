package com.xiaochen.picturebackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Data
public class PictureTagCategory {

    private List<String> tagList;

    private List<String> categoryList;

}
