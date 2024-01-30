package com.xutao.dto.req;

import lombok.Data;

/**
 * @author xutao
 */

@Data
public class UserLoginRepDto {
    private String account;
    private String password;
    private String sessionId;
    private String chickCode;
    private String emailCode;
}
