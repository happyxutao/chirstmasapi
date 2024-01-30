package com.xutao.controller;


import com.xutao.common.entity.Result;
import com.xutao.dto.req.UserRegisterReqDto;
import com.xutao.service.ResourceService;
import com.xutao.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    ResourceService resourceService;

    @Autowired
    SysUserService sysUserService;

    /**
     * 发送手机短信
     * @param phone
     * @return
     */
    @GetMapping("/sendMsg")
    public Result sendPhoneMsg(String phone){
//        System.out.println(phone);
        return resourceService.sendPhoneMsg(phone);
    }

    /**
     * 验证手机短信是否正确
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/chickPhoneValidateCode")
    public Result chickPhoneValidateCode(String phone,String code){
        return resourceService.chickPhoneValidateCode(phone,code);
    }

    /**
     * 头像上传接口
     */
    @PostMapping("/image")
    public Result uploadImage(MultipartFile file) throws IOException {
        return resourceService.uploadImage(file);
    }

    /**
     * 用户注册
     */
    @PostMapping
    public Result register(@Valid @RequestBody UserRegisterReqDto dto){

        return  sysUserService.register(dto);
    }

}
