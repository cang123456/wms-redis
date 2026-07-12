package com.example.wms001.config;

import com.example.wms001.common.JwtInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    // 文件上传和下载  地址  请求地址
    @Value("${upload.goods.base-path}")
    private String basePath;
    @Value("${upload.goods.access-prefix}")
    private String accessPrefix;
    /**
     * 1. 静态资源映射（给 Knife4j 接口文档用）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        // Knife4j 文档页面
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // 前端静态资源
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // 映射规则：访问 /upload/goods/** 时，自动指向服务器硬盘的 basePath
        registry.addResourceHandler(accessPrefix + "**")
                .addResourceLocations("file:" + basePath);
    }

    /**
     * 2. 添加 JWT 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/user/login",    // 登录接口放行
                        "/doc.html",      // Knife4j 文档页面放行
                        "/webjars/**",    // Knife4j 静态资源放行
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/upload/**"
                );
    }
}