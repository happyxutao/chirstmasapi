package com.xutao.dto.req;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterReqDto {
    @NotBlank(message="手机号不能为空！")
    @Pattern(regexp="^1[3|4|5|6|7|8|9][0-9]{9}$",message="手机号格式不正确！")
    private String phone;

    @NotBlank(message="密码不能为空！")
    private String password;

    @NotBlank(message="姓名不能为空")
    private String name;

    @NotBlank(message="头像不能为空")
    private String avater;

    @NotBlank(message="姓别不能为空")
    private String sex;
    private String birthday;
    private String[] likeFriuts;
    private String[] likeHobbies;
    private String[] likePersonalities;
}
