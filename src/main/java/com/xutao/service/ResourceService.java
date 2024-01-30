package com.xutao.service;


import com.xutao.common.entity.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 资源（图片/视频/文档）相关服务类
 */
public interface ResourceService {

     Result sendMsg(String email);

    /**
     * 获取图片验证码
     *
     * @throws IOException 验证码图片生成失败
     * @return Base64编码的图片
     */
    Result getImgVerifyCode() throws IOException;

    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    Result sendPhoneMsg(String phone);

    /**
     * 验证手机验证码是否正确
     * @return
     */
    Result chickPhoneValidateCode(String phone,String code);

    /**
     * 图片上传
     * @param file 需要上传的图片
     * @return 图片访问路径
     * */
    Result uploadImage(MultipartFile file) throws IOException;



}
