package com.atguigu.yygh.user;

import com.atguigu.yygh.user.prop.WeixinProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.atguigu.yygh")
@MapperScan(value = "com.atguigu.yygh.user.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu")
@EnableConfigurationProperties(value = WeixinProperties.class)
public class UserHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserHospApplication.class,args);
    }
}
