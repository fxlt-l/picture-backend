package com.xiaochen.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaochen.picturebackend.model.dto.picture.PictureQueryRequest;
import com.xiaochen.picturebackend.model.dto.picture.PictureUploadRequest;
import com.xiaochen.picturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaochen.picturebackend.model.entity.User;
import com.xiaochen.picturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 小陈
* @description 针对表【picture(图片表)】的数据库操作Service
* @createDate 2025-12-24 16:13:34
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    void validPicture(Picture picture);
}
