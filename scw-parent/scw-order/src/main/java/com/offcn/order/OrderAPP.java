package com.offcn.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@MapperScan("com.offcn.order.mapper")
@EnableFeignClients
@EnableCircuitBreaker
public class OrderAPP {
    public static void main(String[] args) {
        SpringApplication.run(OrderAPP.class);
    }
}
