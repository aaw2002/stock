package com.itheima.stock.controller;

import com.itheima.stock.domain.vo.resp.PageResult;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.domain.StockBlockDomain;
import com.itheima.stock.pojo.domain.oneStockBusiness;
import com.itheima.stock.pojo.domain.weekStockK;
import com.itheima.stock.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quot")
@Api(tags = "股票数据接口")
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/index/all")
    @ApiOperation("获取国内最新大盘指数")
    public R<List<InnerMarketDomain>> getInnerAllStock(){
       return stockService.getInnerAllStock();
    }

    @GetMapping("sector/all")
    @ApiOperation("查询沪深两市最新的板块行情数据，并按照交易金额降序排序展示前10条记录")
    public R<List<StockBlockDomain>> getInnerAllLSstock(){
        return stockService.getInnerAllLSstock();
    }

    @ApiOperation("分页获取沪深板块的实时数据并根据涨幅降序展示")
    @GetMapping("stock/all")
    public R<PageResult> getStockUpdown(@RequestParam (name = "page",required = false,defaultValue = "1")Integer page,
                                        @RequestParam (name = "pageSize",required = false,defaultValue = "10")Integer pageSize){
        return stockService.getStockUpdownPageInfo(page,pageSize);
    }
    /**
     * 将指定页的股票数据导出到excel表下
     * @param response
     * @param page  当前页
     * @param pageSize 每页大小
     */
    @ApiOperation("将指定页的股票数据导出到excel表下")
    @GetMapping("stock/export")
    public void exportStockUpdown(@RequestParam (name = "page",required = false,defaultValue = "1")Integer page,
                                        @RequestParam (name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                  HttpServletResponse response) throws IOException {
        stockService.exportStockUpdown(page,pageSize,response);

    }

    /**
     * 统计A股大盘T日和T-1日成交量对比功能
     */
    @ApiOperation("统计A股大盘T日和T-1日成交量对比功能")
    @GetMapping("stock/tradeAmt")
    public R<Map> selectTradeAmtContrast(){
        return stockService.selectTradeAmtContrast();
    }
    /**
     * 统计当前时间下，A股在各个涨跌区间股票的数量
     */
    @GetMapping("stock/updown")
    public R<Map> getRFStockinfo(){
        return stockService.getRFStockinfo();
    }

    /**
     * 查询个股的分时行情数据，统计指定股票T日每分钟的交易数据
     */
    @ApiOperation("查询个股的分时行情数据，统计指定股票T日每分钟的交易数据")
    @GetMapping("stock/screen/screen/time-sharing")
    public R<List> getStockScreenTimeSharing(@RequestParam(name = "code",required = true)String code){

        return stockService.getStockScreenTimeSharing(code);
    }
    /**
     * 查询指定股票每天产生的数据，组成日k线数据
     * 默认10天
     */
    @GetMapping("stock/screen/screen/dkline")
    @ApiOperation("查询指定股票每天产生的数据，组成日k线数据")
    public R<List> getStockScreenDKline(@RequestParam(name = "code",required = true)String code){
        return stockService.getStockScreenDKline(code);
    }

    @GetMapping("external/index")
    @ApiOperation("外盘指数行情数据查询，根据时间和大盘点数降序取前四")
    public R<List> getExternalIndex(){
        return stockService.outerAllStock();
    }

    /**
     * 个股主营业务查询接口
     */
    @GetMapping("stock/describe")
    @ApiOperation("个股主营业务查询接口")
    public R<oneStockBusiness> getStockDescribe(@RequestParam(name = "code",required = true)String code){
        return R.ok(stockService.getStockBusiness(code));
    }
    /**
     * 个股周k线
     */
    @GetMapping("stock/screen/weekkline")
    @ApiOperation("个股周k线")
    public R<weekStockK> getStockScreenWeekKline(@RequestParam(name = "code",required = true)String code){

        stockService.getStockScreenWeekKline(code);
        return R.ok(stockService.getStockScreenWeekKline(code));
    }

}
