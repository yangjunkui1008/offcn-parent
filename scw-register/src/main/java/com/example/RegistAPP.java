package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer  //开启eureka注册服务中心
public class RegistAPP {

    public static void main(String[] args) {
        SpringApplication.run(RegistAPP.class, args);
    }

}
