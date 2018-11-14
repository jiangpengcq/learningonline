package com.xuecheng.manage.cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * OkHttpClient测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class OkHttpClientTest {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试Java Http请求
     */
    @Test
    public void testOkHttpClient(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5be818f59d941f21183c8f6e", Map.class);
        System.out.println(forEntity.getBody());
    }
}
