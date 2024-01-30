package com.xutao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xutao.common.constant.Base;
import com.xutao.common.constant.ErrorCodeEnum;
import com.xutao.common.entity.Result;
import com.xutao.common.exception.BusinessException;
import com.xutao.common.manager.redis.VerifyCodeManager;
import com.xutao.common.utils.JwtUtils;
import com.xutao.dao.entity.SysUser;
import com.xutao.dao.entity.UserHobbies;
import com.xutao.dao.mapper.SysUserMapper;
import com.xutao.dto.req.UserLoginRepDto;
import com.xutao.dto.req.UserRegisterReqDto;
import com.xutao.dto.resp.UserLoginRespDto;
import com.xutao.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xutao.service.UserHobbiesService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author xuTao
 * @since 2024/01/07
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    VerifyCodeManager verifyCodeManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserHobbiesService userHobbiesService;

    @Override
    public Result login(UserLoginRepDto dto) {
        //1.判断前端的验证码是否正确
        if(!verifyCodeManager.imgVerifyCodeOk(dto.getSessionId(), dto.getChickCode())){
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        }
        //2.查找数据库，看账号与密码是否匹配
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getName,dto.getAccount());
        SysUser user = this.getOne(sysUserLambdaQueryWrapper);
        if(Objects.isNull(user)){
            //用户不存在
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        //判断密码是否正确
        if(!Objects.equals(user.getPassword(),dto.getPassword())){
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        //登录成功，生成jwt并返回
        return Result.success(
                UserLoginRespDto.builder()
                        .token(jwtUtils.generateToken(user.getId(), Base.CURRENT_USER))
                        .uid(user.getId())
                        .nickName(user.getUsername()).build()
        );

    }

    @Override
    public Result qqEmailLogin(UserLoginRepDto dto) {
        String email = dto.getAccount();
        //0.判断前端的验证码是否正确
        if(!verifyCodeManager.imgVerifyCodeOk(dto.getSessionId(), dto.getChickCode())){
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        }
        //1. 查找数据库，查看邮箱有没有进行过注册
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getEmail, email);
        SysUser user = this.getOne(sysUserLambdaQueryWrapper);
        //1.1 注册过，进行下一步；没注册，抛出异常，提示用户未注册
        if(Objects.isNull(user)){
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        //2.判断用户输入的qq验证码是否正确
        //2.1 根据qq邮箱，在redis查找出验证码，在进行对比
        String qqVerifyCode = verifyCodeManager.getQqVerifyCode(email);
        if(Objects.isNull(qqVerifyCode)){
            //没有找到验证码：1.验证码过期 2.没有发送过验证码;抛出验证码失效错误
            throw new BusinessException(ErrorCodeEnum.USER_QQ_CODE_INVALID);
        }
        if(!Objects.equals(qqVerifyCode,dto.getEmailCode())){
            throw new BusinessException(ErrorCodeEnum.USER_QQ_CODE_ERROR);
        }
        //3. 包装数据返回结果，生成jwt返回
        //登录成功，生成jwt并返回
        return Result.success(
                UserLoginRespDto.builder()
                        .token(jwtUtils.generateToken(user.getId(), Base.CURRENT_USER))
                        .uid(user.getId())
                        .nickName(user.getUsername()).build()
        );
    }

    @Override
    public Result register(UserRegisterReqDto dto) {
        //1.用户是否已经注册
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getMobile,dto.getPhone());
        SysUser user = this.getOne(sysUserLambdaQueryWrapper);
        if(!Objects.isNull(user)){
            throw new BusinessException(ErrorCodeEnum.USER_PHONE_EXIST);
        }
        //2.插入用户个人信息
        SysUser userDao = new SysUser();
        userDao.setMobile(dto.getPhone());
        userDao.setPassword(dto.getPassword());
        userDao.setName(dto.getName());
        userDao.setAvater(dto.getAvater());
        userDao.setSex(dto.getSex()=="男"? 0:1);
        // 解析为 Instant
        Instant instant = Instant.parse(dto.getBirthday());
        // 转换为系统默认时区的 LocalDateTime
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        userDao.setBirth(localDateTime);
        userDao.setUsername(dto.getName());
        boolean isSave = this.save(userDao);
        if(!isSave){
            //抛出异常
            throw new BusinessException(ErrorCodeEnum.SYSTEM_TIMEOUT_ERROR);
        }
        //3.插入用户爱好
        //3.1查找刚刚插入的用户id
        SysUser sysUser=this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getMobile,dto.getPhone()));
        //2.2提取用户爱好数据，dto->UserHobbies
        String[] friuts = dto.getLikeFriuts();
        String[] hobbies = dto.getLikeHobbies();
        String[] personalities = dto.getLikePersonalities();
        String fruitsString = (friuts != null) ? String.join(",", friuts) : "";
        String hobbiesString = (hobbies != null) ? String.join(",", hobbies) : "";
        String personaString = (personalities != null) ? String.join(",",personalities): "";
        UserHobbies userHobbies = new UserHobbies();
        userHobbies.setHobby(hobbiesString);
        userHobbies.setFruit(fruitsString);
        userHobbies.setPersonality(personaString);//还有用户信息
        userHobbies.setUserId(sysUser.getId());
        boolean save = userHobbiesService.save(userHobbies);
        if(!save){
            throw new BusinessException(ErrorCodeEnum.SYSTEM_TIMEOUT_ERROR);
        }
        //4.包装结果，返回数据
        return Result.success(UserLoginRespDto.builder()
                .token(jwtUtils.generateToken(sysUser.getId(), Base.CURRENT_USER))
                .uid(sysUser.getId())
                .nickName(sysUser.getName()).build());
    }
}
