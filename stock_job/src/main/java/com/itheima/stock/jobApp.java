package com.itheima.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.itheima.stock.mapper")
@SpringBootApplication
public class jobApp {
    public static void main(String[] args) {
        SpringApplication.run(jobApp.class, args);
    }
}
