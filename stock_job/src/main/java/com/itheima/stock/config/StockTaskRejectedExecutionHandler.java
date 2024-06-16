package com.itheima.stock.config;

import com.itheima.stock.domain.StockTaskRunable;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author by itheima
 * @Date 2022/1/7
 * @Description 自定义线程池任务拒绝策略
 */
@Slf4j
public class StockTaskRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (r instanceof StockTaskRunable) {
            StockTaskRunable r2= ((StockTaskRunable) r);
            Map<String, Object> infos = r2.getInfos();
            log.info("出现的异常的任务信息：{}",infos);
        }
    }
}