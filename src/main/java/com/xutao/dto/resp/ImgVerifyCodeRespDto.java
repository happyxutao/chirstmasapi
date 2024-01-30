package com.xutao.dto.resp;


import lombok.Builder;
import lombok.Data;

/**
 * 图像验证码 响应DTO
 */
@Data
@Builder
public class ImgVerifyCodeRespDto {

    /**
     * 当前会话ID，用于标识改图形验证码属于哪个会话
     * */

    private String sessionId;

    /**
     * Base64 编码的验证码图片
     * */

    private String img;

    /**
     * 返回给前端的验证码
     */
    private String code;
}
