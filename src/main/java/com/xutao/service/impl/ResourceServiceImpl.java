package com.xutao.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.xutao.common.constant.Base;
import com.xutao.common.constant.CacheConsts;
import com.xutao.common.constant.ErrorCodeEnum;
import com.xutao.common.exception.BusinessException;
import com.xutao.common.manager.mail.PhoneEmailManager;
import com.xutao.common.manager.mail.QqEmailManager;
import com.xutao.common.manager.redis.VerifyCodeManager;
import com.xutao.common.entity.Result;
import com.xutao.common.utils.ValidateCodeUtils;
import com.xutao.dto.resp.ImgVerifyCodeRespDto;
import com.xutao.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 资源（图片/视频/文档）相关服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private final VerifyCodeManager verifyCodeManager;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    QqEmailManager qqEmailManager;

    @Autowired
    PhoneEmailManager phoneEmailManager;

    @Value("${chirstmas.file.upload.path}")
    private String fileUploadPath;


    @Override
    public Result sendMsg(String email) {
        if(!Objects.isNull(email)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String text = Base.MSG1 + code + Base.MSG2 ;
            //发送信息
            qqEmailManager.sendEmail(email,Base.APP,text);
            //将验证码存储在redis中
            redisTemplate.opsForValue().set(CacheConsts.QQ_MAIL_VERIFY_CACHE_KEY+email,code,5, TimeUnit.MINUTES);
            return Result.success("验证码发送成功");
        }
        return Result.error(ErrorCodeEnum.QQ_VERIFICATION_CODE_SEND_ERROR);
    }

    @Override
    public Result getImgVerifyCode() throws IOException {
        String sessionId = IdWorker.get32UUID();
        return Result.success(ImgVerifyCodeRespDto.builder()
                .sessionId(sessionId)
                .img(verifyCodeManager.genImgVerifyCode(sessionId))
                .code(redisTemplate.opsForValue().get(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId))
                .build());
    }

    @Override
    public Result sendPhoneMsg(String phone) {
        if(!Objects.isNull(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String text = Base.MSG1 + code + Base.MSG2 ;
            phoneEmailManager.sendPhoneMsg(phone,Base.APP,text);
            //将验证码存储在redis中
            redisTemplate.opsForValue().set(CacheConsts.PHONE_MAIL_VERIFY_CACHE_KEY+phone,code,5, TimeUnit.MINUTES);
            return Result.success("验证码发送成功");
        }
        return Result.error(ErrorCodeEnum.Phone_VERIFICATION_CODE_SEND_ERROR);
    }

    @Override
    public Result chickPhoneValidateCode(String phone,String code) {
        //1. 验证手机和验证码是否为空
        if(Objects.isNull(phone)||Objects.isNull(code)){
            //todo 抛出业务异常
            throw new BusinessException(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
        }
        //2. 从redis中取出phone对应的验证码
        String trueCode=redisTemplate.opsForValue().get(CacheConsts.PHONE_MAIL_VERIFY_CACHE_KEY+phone);
        //3. 验证code和redis中存储的是否一致
        if(!Objects.equals(trueCode,code)){
            //todo 抛出业务异常
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        }
        //返回正确结果
        return Result.success("手机验证正确");
    }

    @Override
    public Result uploadImage(MultipartFile file) throws IOException{
        LocalDateTime now = LocalDateTime.now();
        String savePath =
                Base.IMAGE_UPLOAD_DIRECTORY
                        + now.format(DateTimeFormatter.ofPattern("yyyy")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("MM")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("dd"));
        String oriName = file.getOriginalFilename();
        assert oriName != null;
        String saveFileName = IdWorker.get32UUID() + oriName.substring(oriName.lastIndexOf("."));
        File saveFile = new File(fileUploadPath + savePath, saveFileName);
        System.out.println(saveFile.getParentFile());
        if (!saveFile.getParentFile().exists()) {
            boolean isSuccess = saveFile.getParentFile().mkdirs();
            if (!isSuccess) {
                throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
            }
        }
        file.transferTo(saveFile);
        if (Objects.isNull(ImageIO.read(saveFile))) {
            // 上传的文件不是图片
            Files.delete(saveFile.toPath());
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_TYPE_NOT_MATCH);
        }
        return Result.success(savePath + File.separator + saveFileName);
    }


}
