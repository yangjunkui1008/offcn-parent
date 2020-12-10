package com.offcn.user.test;

import com.offcn.UserAPP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserAPP.class})
public class ScwuserApplicationDemo {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){
        redisTemplate.opsForValue().set("测试","测试内容");
    }
}
