package com.xuecheng.manage.cmsclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xuecheng.manage.cmsclient")//扫描本类同级目录下的类
@ComponentScan(basePackages = {"com.xuecheng.framework"})//扫描common包下的类
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
