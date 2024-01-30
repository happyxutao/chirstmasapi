## 登录流程



1. 前端发送请求获取后端的验证码

   ```vue
   export const getCaptcha = () => {
       return request.get('login/captcha')
   }
   ```

2. 后端接受请求产生验证码，存入redis中，用jwt产生验证码

   ```java
   /**
        * 生成图形验证码，并放入 Redis 中
        */
       public String genImgVerifyCode(String sessionId) throws IOException {
           String verifyCode = ImgVerifyCodeUtils.getRandomVerifyCode(4);
           String img = ImgVerifyCodeUtils.genVerifyCodeImg(verifyCode);
           stringRedisTemplate.opsForValue().set(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId,
               verifyCode, Duration.ofMinutes(5));
           return img;
       }
   ```

   3.后端返回图片数据，并携带会话标识sessionId,和验证码方便给前端进行校验

   ```java
   @Override
       public Result getImgVerifyCode() throws IOException {
           String sessionId = IdWorker.get32UUID();
           return Result.success(ImgVerifyCodeRespDto.builder()
                   .sessionId(sessionId)
                   .img(verifyCodeManager.genImgVerifyCode(sessionId))
                 .code(redisTemplate.opsForValue().get(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId))
                   .build());
       }
   
   ```

   4. 前端收到后端的验证码数据，并展示

      ```vue
      async getCaptcha() {
                  const {
                      data: {
                          img,
                          sessionId,
                          code
                      }
                  } = await getCaptcha()
                  this.ruleForm.sessionId = sessionId
                  this.captchaImageUrl = "data:image/png;base64," + img
                  this.responseCode = code
              }
      ```

      5.用户填写好登录表单，便可登录

      ![image-20240119213525422](C:\Users\xutao\AppData\Roaming\Typora\typora-user-images\image-20240119213525422.png)

   6. 前端先进行表单的格式验证，在将表单信息发送给后端

      - 表单前端表单验证

        ```vue
        validFn() {
                    if (this.passwordLogin) {
                        // 密码格式：密码至少包含8个字符，且其中至少有一个字母和一个数字
                        if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(this.ruleForm.password)) {
                            this.$alert('请输入正确的密码')
                            return false
                        }
                    } else {
                        //验证邮箱格式
                        if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(this.ruleForm.account)) {
                            this.$alert('邮箱格式错误')
                            return false
                        }
                    }
                    if (!/^\w{4}$/.test(this.ruleForm.chickCode)) {
                        this.$alert('请输入正确的验证码')
                        return false
                    }
                    //验证输入的验证码和后台返回的验证码是否一致
                    if (this.responseCode != this.ruleForm.chickCode) {
                        this.$alert('验证码错误')
                        return false
                    }
                    return true
                }
        ```

      - 前端发送登录逻辑

        ```vue
        async login() {
                    const vm = this // 保存当前 Vue 实例的引用
                    this.$refs['ruleForm'].validate(async (valid) => {
                        if (!valid || !vm.validFn()) {
                            return
                        }
                        if (vm.ruleForm.emailCode != '') {
                            if (!/^\d{4}$/.test(vm.ruleForm.emailCode)) {
                                this.$alert('请输入正确的邮箱验证码')
                                return
                            }
                        }
                        const res = await accountLogin(vm.ruleForm)
                        if (res.code == '00000') {
                            vm.$store.dispatch('user/setUserInfo', res.data)
                            vm.$router.push('/home')
                        }
                    })
        
                },
        ```

        - 前端要发送给后端的数据

          ```vue
          ruleForm: {
                          account: '',
                          password: '',
                          chickCode: '',
                          emailCode: '',
                          sessionId: ''
                      },
          ```

          

   7. 后端用一个实体类接受前端传过来的参数，在进行信息校验，校验成功返回用户信息给前端并携带token

      ```java
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
      
      ```

## qq邮箱验证码登录

1. 发送qq验证码请求

   ```vue
   async sendEmail() {
               if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(this.ruleForm.account)) {
                   this.$alert('邮箱格式错误')
                   return false
               }
               //如果没有点击过，而且没有开启倒计时
               if (!this.timer && this.second === this.totalSecond) {
                   const res =await sendQQValidateCode(this.ruleForm.account);
                   console.log(res)
                   if (res.code == '00000') {
                       this.$alert('验证码已发送至您的邮箱，请注意查收')
                   } else {
                       this.$alert(res.data)
                   }
                   this.timer = setInterval(() => {
                       this.second--
                       if (this.second < 1) {
                           clearInterval(this.timer)
                           this.timer = null
                           this.second = this.totalSecond
                       }
                   }, 1000)
               }
           }
   ```

   

2. 后端对应的服务类

   ```java
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
   ```

   

3. 后端根据字段判断登入逻辑

```java
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
```



4. qq登入服务类

```java
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
```

## springboot实现语音发送



1. 建立pom文件

2. 我理解方式

   ```java
   ```

   ![image-20240120135941656](C:\Users\xutao\AppData\Roaming\Typora\typora-user-images\image-20240120135941656.png)

- 

- 

