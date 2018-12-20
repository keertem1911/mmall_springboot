package com.xiyou.edu.sessionserver;

import com.xiyou.edu.sessionserver.bean.CCPRestSmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author 王传鑫
 * @Date 2018/12/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CCPServiceTest {
    @Autowired
    private CCPRestSmsConfig ccpRestSmsConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void read(){

        stringRedisTemplate.opsForValue().set("eket","1122",60, TimeUnit.SECONDS);
    }
}
