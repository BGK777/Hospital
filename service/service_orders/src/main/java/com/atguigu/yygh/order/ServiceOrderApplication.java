package com.atguigu.yygh.order;

import com.atguigu.yygh.order.prop.WeixinPay;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu"})
@MapperScan(value = "com.atguigu.yygh.order.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atguigu"})
@EnableConfigurationProperties(value = WeixinPay.class)
public class ServiceOrderApplication {
 public static void main(String[] args) {
      SpringApplication.run(ServiceOrderApplication.class, args);
  }
}