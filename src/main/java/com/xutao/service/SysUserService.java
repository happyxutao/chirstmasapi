package com.xutao.service;

import com.xutao.common.entity.Result;
import com.xutao.dao.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xutao.dto.req.UserLoginRepDto;
import com.xutao.dto.req.UserRegisterReqDto;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author xuTao
 * @date 2024/01/07
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 普通登录方式
     * @param dto
     * @return
     */
    Result login(UserLoginRepDto dto);

    /**
     * qq邮箱登录方式
     * @param dto
     * @return
     */
    Result qqEmailLogin(UserLoginRepDto dto);

    /**
     * 用户注册
     * @param dto
     * @return
     */
    Result register(UserRegisterReqDto dto);
}
