package com.itheima.stock.service;

public interface StockTimerTaskService {
    /**
     * 获取国内大盘的实时数据信息
     */
    void getInnerMarketData();
    /**
     * 获取国内个股的时时数据信息并批量插入
     */
    void getStockRtIndex();
    /**
     *
     */
    void getStockBlockData();


    void stockRtInto();
}
