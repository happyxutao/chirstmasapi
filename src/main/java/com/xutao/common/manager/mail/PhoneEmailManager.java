package com.xutao.common.manager.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhoneEmailManager {
    public void sendPhoneMsg(String phone,String subject,String text){
        //todo send
        System.out.println(phone + "已发送");
    }

}
