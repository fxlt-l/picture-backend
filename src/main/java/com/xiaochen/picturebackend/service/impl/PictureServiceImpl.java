package com.xiaochen.picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaochen.picturebackend.common.ErrorCode;
import com.xiaochen.picturebackend.constant.CommonConstant;
import com.xiaochen.picturebackend.exception.ThrowUtils;
import com.xiaochen.picturebackend.manager.FileManager;
import com.xiaochen.picturebackend.model.dto.file.UploadPictureResult;
import com.xiaochen.picturebackend.model.dto.picture.PictureQueryRequest;
import com.xiaochen.picturebackend.model.dto.picture.PictureUploadRequest;
import com.xiaochen.picturebackend.model.entity.Picture;
import com.xiaochen.picturebackend.model.entity.User;
import com.xiaochen.picturebackend.model.vo.PictureVO;
import com.xiaochen.picturebackend.model.vo.UserVO;
import com.xiaochen.picturebackend.service.PictureService;
import com.xiaochen.picturebackend.mapper.PictureMapper;
import com.xiaochen.picturebackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 小陈
* @description 针对表【picture(图片表)】的数据库操作Service实现
* @createDate 2025-12-24 16:13:34
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 用于判断是新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }

    /**
     * 根据查询条件获取查询构造器
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        LambdaQueryWrapper<Picture> queryWrapper = new LambdaQueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            // 使用 Lambda 表达式
            queryWrapper.and(qw ->
                    qw.like(Picture::getName, searchText)
                            .or()
                            .like(Picture::getIntroduction, searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), Picture::getId, id)
                .eq(ObjUtil.isNotEmpty(userId), Picture::getUserId, userId)
                .like(StrUtil.isNotBlank(name), Picture::getName, name)
                .like(StrUtil.isNotBlank(introduction), Picture::getIntroduction, introduction)
                .like(StrUtil.isNotBlank(picFormat), Picture::getPicFormat, picFormat)
                .eq(StrUtil.isNotBlank(category), Picture::getCategory, category)
                .eq(ObjUtil.isNotEmpty(picWidth), Picture::getPicWidth, picWidth)
                .eq(ObjUtil.isNotEmpty(picHeight), Picture::getPicHeight, picHeight)
                .eq(ObjUtil.isNotEmpty(picSize), Picture::getPicSize, picSize)
                .eq(ObjUtil.isNotEmpty(picScale), Picture::getPicScale, picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like(Picture::getTags, "\"" + tag + "\"");
            }
        }
        // 排序白名单
        if (StrUtil.equalsAny(sortField, "id", "name", "createTime", "updateTime",
                "picSize", "picWidth", "picHeight", "picScale",
                "userId", "category","picFormat", "introduction")) {

            SFunction<Picture, ?> col = switch (sortField) {
                case "name" -> Picture::getName;
                case "createTime" -> Picture::getCreateTime;
                case "updateTime" -> Picture::getUpdateTime;
                case "picSize" -> Picture::getPicSize;
                case "picWidth" -> Picture::getPicWidth;
                case "picHeight" -> Picture::getPicHeight;
                case "picScale" -> Picture::getPicScale;
                case "userId" -> Picture::getUserId;
                case "category" -> Picture::getCategory;
                case "picFormat" -> Picture::getPicFormat;
                case "introduction" -> Picture::getIntroduction;
                default -> Picture::getId;
            };
            if(StrUtil.equals(sortOrder, CommonConstant.SORT_ORDER_ASC)) {
                queryWrapper.orderByAsc(col);
            } else {
                queryWrapper.orderByDesc(col);
            }
        }
        return queryWrapper;
    }


    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }




}




