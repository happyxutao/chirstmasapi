package com.xutao.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 * 用户登录 响应DTO
 */
@Data
@Builder
public class UserLoginRespDto {

    private Long uid;

    private String nickName;

    private String token;
}
