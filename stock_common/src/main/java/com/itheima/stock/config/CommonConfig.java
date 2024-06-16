package com.itheima.stock.config;

import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.utils.SnowflakeIdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StockInfoConfig.class)
public class CommonConfig {
    /**
     * 雪花算法
     * id生成
     */

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        // 1,2 代表机器id和数据中心id
        return new SnowflakeIdWorker(1, 2);
    }

}
