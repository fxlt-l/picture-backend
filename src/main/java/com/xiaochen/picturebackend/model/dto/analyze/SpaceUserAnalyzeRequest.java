package com.xiaochen.picturebackend.model.dto.analyze;

import com.xiaochen.picturebackend.common.SpaceAnalyzeRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 小陈
 * @version 1.0
 * @description: TODO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;
}
