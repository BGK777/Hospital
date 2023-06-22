package com.atguigu.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket getAdminDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档描述了后台管理系统微服务接口定义")
                .version("1.0")
//                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }

    @Bean
    public Docket getUserDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .apiInfo(userApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    private ApiInfo userApiInfo(){
        return new ApiInfoBuilder()
                .title("用户系统-API文档")
                .description("本文档描述了用户系统微服务接口定义")
                .version("1.0")
//                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }

    @Bean
    public Docket getApiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(ApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo ApiInfo(){
        return new ApiInfoBuilder()
                .title("第三方系统-API文档")
                .description("本文档描述了第三方系统微服务接口定义")
                .version("1.0")
//                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }
}
