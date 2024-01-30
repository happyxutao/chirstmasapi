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
