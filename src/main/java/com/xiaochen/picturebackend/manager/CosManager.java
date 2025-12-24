package com.xiaochen.picturebackend.manager;

import com.qcloud.cos.COSClient;
import com.xiaochen.picturebackend.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    // ... 一些操作 COS 的方法
}
