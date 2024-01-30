package com.xutao.service;

import com.xutao.dao.entity.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    SysUserService userService;
    @Test
    public void testAddUser() throws Exception {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("chuachua");
        user.setPassword("randomPassword");
        user.setName("Chua Chua");
        user.setSex(1); // 假设 1 代表女性
        user.setBirth(LocalDateTime.of(2000, 5, 10, 0, 0)); // 假设出生日期是 2000 年 5 月 10 日
        user.setEmail("chuachua@example.com");
        user.setMobile("1234567890");
        user.setStatus(1); // 假设 1 代表正常状态
        user.setCreateTime(LocalDateTime.now()); // 设置当前时间为创建时间
        user.setUpdateTime(LocalDateTime.now()); // 设置当前时间为更新时间
        userService.save(user);
    }

    @Test
    public void testParseTime() throws Exception {
        String time =
                "2024-01-16T16:00:00.000Z";
        // 解析为 Instant
        Instant instant = Instant.parse(time);
        // 转换为系统默认时区的 LocalDateTime
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println(localDateTime);
    }
}
