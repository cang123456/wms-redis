package com.example.wms001;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.wms001.mapper")
public class Wms001Application {

    public static void main(String[] args) {
        SpringApplication.run(Wms001Application.class, args);
    }

}
