package com.campus.property;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.property.mapper")
public class CampusPropertyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPropertyApplication.class, args);
    }
}
