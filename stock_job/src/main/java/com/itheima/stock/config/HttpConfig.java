package com.itheima.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {
    /**
     * 定义http客户端
     * @return
     */
    @Bean
public RestTemplate restTemplate(){
    return new RestTemplate();
}
}
