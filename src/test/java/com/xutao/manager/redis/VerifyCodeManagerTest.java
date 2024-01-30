package com.xutao.manager.redis;

import com.xutao.common.manager.redis.VerifyCodeManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class VerifyCodeManagerTest {
    @Autowired
    VerifyCodeManager verifyCodeManager;

    @Test
    public void TestGenImg() throws IOException {
        String s = verifyCodeManager.genImgVerifyCode("1111");
        System.out.println(s);
    }
}
