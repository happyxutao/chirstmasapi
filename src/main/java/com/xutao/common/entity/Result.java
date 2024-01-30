package com.xutao.common.entity;

import com.xutao.common.constant.ErrorCodeEnum;
import lombok.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * api接口数据返回封装
 * @author xutao
 */


@Data
public class Result implements Serializable {

    private static final long serialVersionUID = -4762928619495260423L;

    /**
     *响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private Object data;

    public Result() {
        this.code = ErrorCodeEnum.OK.getCode();
        this.msg = ErrorCodeEnum.OK.getMessage();
    }

    public Result(ErrorCodeEnum errorCodeEnum) {
        this.code = errorCodeEnum.getCode();
        this.msg = errorCodeEnum.getMessage();
    }

    public Result(Object data){
        this();
        this.data=data;
    }

    /**
     * 业务处理成功，无数据返回
     * @return
     */
    public static Result success() {
        return new Result();
    }

    /**
     * 业务处理成功，有数据返回
     * @param data
     * @return
     */
    public static Result success(Object data) {
        return new Result(data);
    }

    /**
     * 业务处理失败
     * @param errorCodeEnum
     * @return
     */
    public static Result error(ErrorCodeEnum errorCodeEnum) {
        return new Result(errorCodeEnum);
    }

    /**
     * 系统错误
     * @return
     */
    public static Result error(){
        return new Result(ErrorCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 判断是否成功
     * @return
     */
    public boolean isOk(){
        return Objects.equals(this.code,ErrorCodeEnum.OK.getCode());
    }

    public Map<String, Object> simple() {
        Map<String, Object> simple = new HashMap<String, Object>();
        this.data = simple;
        return simple;
    }

}