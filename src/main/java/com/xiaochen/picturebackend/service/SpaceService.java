package com.xiaochen.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaochen.picturebackend.model.dto.space.SpaceAddRequest;
import com.xiaochen.picturebackend.model.dto.space.SpaceQueryRequest;
import com.xiaochen.picturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaochen.picturebackend.model.entity.User;
import com.xiaochen.picturebackend.model.vo.SpaceVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小陈
 * @description 针对表【space(图片表)】的数据库操作Service
 * @createDate 2025-12-24 16:13:34
 */
public interface SpaceService extends IService<Space> {


    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    LambdaQueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    void validSpace(Space space, boolean add);

    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);
}
