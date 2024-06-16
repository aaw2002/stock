package com.itheima.stock.domain;

import com.itheima.stock.service.StockTimerTaskService;

import java.util.Map;

public class StockTaskRunable implements Runnable{

    //携带的任务信息,任务拒绝时，使用
    private Map<String,Object> infos;

    private StockTimerTaskService stockTimerService;

    public StockTaskRunable(Map<String, Object> infos, StockTimerTaskService stockTimerService) {
        this.infos = infos;
        this.stockTimerService = stockTimerService;
    }

    //提供get方法
    public Map<String, Object> getInfos() {
        return infos;
    }

    //任务逻辑
    @Override
    public void run() {
        stockTimerService.stockRtInto();
    }
}