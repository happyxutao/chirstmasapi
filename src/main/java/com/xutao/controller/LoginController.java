package com.xutao.controller;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.xutao.common.constant.ErrorCodeEnum;
import com.xutao.common.entity.Result;
import com.xutao.common.exception.BusinessException;
import com.xutao.dto.req.UserLoginRepDto;
import com.xutao.service.ResourceService;
import com.xutao.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * @author xutao
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    SysUserService userService;
    @Autowired
    ResourceService resourceService;
    @PostMapping
    public Result login(@RequestBody UserLoginRepDto dto){
        // 检查必要的字段
        if(dto.getAccount() == null || dto.getChickCode() == null || dto.getSessionId() == null){
            throw new BusinessException(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
        }
        // 邮箱登录：密码为空，且邮箱验证码不为空
        if(ObjectUtils.isEmpty(dto.getPassword()) && !Objects.isNull(dto.getEmailCode())){
            return userService.qqEmailLogin(dto);
        }
        // 密码登录：密码不为空
        if(!Objects.isNull(dto.getPassword())){
            return userService.login(dto);
        }
        // 如果既没有密码也没有邮箱验证码，则抛出请求参数错误
        throw new BusinessException(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
    }

    @GetMapping("/captcha")
    public Result getCaptcha() throws IOException {
        return  resourceService.getImgVerifyCode();
    }

    @GetMapping("/sendMsg")
    public Result sendMsg(String email) throws IOException{
        return resourceService.sendMsg(email);
    }
}
