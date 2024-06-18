package com.itheima.stock.service;

import com.itheima.stock.domain.vo.resp.PageResult;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.domain.StockBlockDomain;
import com.itheima.stock.pojo.domain.oneStockBusiness;
import com.itheima.stock.pojo.domain.weekStockK;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StockService {
    /**
     * 获取国内指定时间股票信息
     *
     * @return
     */
    R<List<InnerMarketDomain>> getInnerAllStock();

    //查询沪深两市最新的板块行情数据，并按照交易金额降序排序展示前10条记录
    R<List<StockBlockDomain>> getInnerAllLSstock();

    //分页获取沪深板块的实时数据并根据涨幅降序展示
    R<PageResult> getStockUpdownPageInfo(Integer page, Integer pageSize);


    /**
     * 将指定页的股票数据导出到excel表下
     *
     * @param response
     * @param page     当前页
     * @param pageSize 每页大小
     */
    void exportStockUpdown(Integer page, Integer pageSize, HttpServletResponse response) throws IOException;

    /**
     * 统计A股大盘T日和T-1日成交量对比功能
     */
    R<Map> selectTradeAmtContrast();

    /**
     * 统计当前时间下，A股在各个涨跌区间股票的数量
     */
    R<Map> getRFStockinfo();

    /**
     * 查询个股的分时行情数据，统计指定股票T日每分钟的交易数据
     */
    R<List> getStockScreenTimeSharing(String code);

    /**
     * 查询指定股票每天产生的数据，组成日k线数据
     */
    R<List> getStockScreenDKline(String code);

    /**
     * 查询外部大盘指数
     */
    R<List> outerAllStock();

    /**
     * 根据输入的个股代码模糊搜索
     */
    R<List> vagueSearch(String searchStr);

    /**
     * 个股主营业务查询接口
     */
    oneStockBusiness getStockBusiness(String code);

    /**
     * 个股周k线
     */
    R<weekStockK> getStockScreenWeekKline(String code);

}
