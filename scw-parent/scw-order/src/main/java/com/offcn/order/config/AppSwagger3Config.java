package com.offcn.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class AppSwagger3Config {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
            .apis(RequestHandlerSelectors.basePackage("com.offcn.order.controller"))
            .paths(PathSelectors.any())
            .build();
         }

         public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("七易众筹-订单模块")
                .description("用户展示订单模块/支付模块的文档")
                .termsOfServiceUrl("http://www.ujiuye.com/")
                .version("1.0")
                .build();
         }
}
