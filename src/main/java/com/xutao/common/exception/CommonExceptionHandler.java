package com.xutao.common.exception;

import com.xutao.common.constant.ErrorCodeEnum;
import com.xutao.common.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 通用的异常处理器
 *
 * @author xiongxiaoyang
 * @date 2022/5/11
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * 处理数据校验异常
     */
    @ExceptionHandler(BindException.class)
    public Result handlerBindException(BindException e) {
        log.error(e.getMessage(), e);
        return Result.error(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result handlerBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getErrorCodeEnum());
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error();
    }

}
