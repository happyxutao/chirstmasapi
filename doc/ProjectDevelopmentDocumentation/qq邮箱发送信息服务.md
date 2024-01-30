## qq邮箱发送信息服务



1. 添加pom服务

   ```java
   <!--qq邮箱-->
       <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-mail</artifactId>
       </dependency>
   ```

2. 配置yaml

   ```yaml
   spring:
     mail:
       # 配置 SMTP 服务器地址
       host: smtp.qq.com
       # 发送者邮箱
       username: 1126556181@qq.com
       # 配置密码，注意不是真正的密码，而是刚刚申请到的授权码
       password: qmfpdvzzdgywbaee
       # 端口号465或587
       port: 587
       # 默认的邮件编码为UTF-8
       default-encoding: UTF-8
       # 配置SSL 加密工厂
       properties:
         mail:
           smtp:
             socketFactoryClass: javax.net.ssl.SSLSocketFactory
           #表示开启 DEBUG 模式，这样，邮件发送过程的日志会在控制台打印出来，方便排查错误
           debug: true
   ```

   

3. 配置服务代理

```java
package com.xutao.common.manager.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QqEmailManager {
    //发送邮箱需要的对象
    private final JavaMailSender javaMailSender;
    //把yml配置的邮箱号赋值到from
    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String to,String subject,String text){
        //发送简单邮件，简单邮件不包括附件等别的
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        //发送邮件
        javaMailSender.send(message);
    }
}

```

