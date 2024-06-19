package com.itheima.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.stock.domain.vo.resp.PageResult;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.domain.vo.resp.ResponseCode;
import com.itheima.stock.mapper.*;
import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockService;
import com.itheima.stock.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private Cache<String,Object> caffeineCache;
    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;
    /**
     * 获取国内最新时间股票信息
     *
     * @return
     */
    @Override
    public R<List<InnerMarketDomain>> getInnerAllStock() {
        R<List<InnerMarketDomain>> date = (R<List<InnerMarketDomain>>) caffeineCache.get("innerMarketInfos", key -> {
            //获取所有国内A股编码
            List<String> inners = stockInfoConfig.getInner();
            //获取当前股票交易时间
            Date lastDate = DateTimeUtils.getLastDate4Stock(DateTime.now()).toDate();
        //3.将获取的java Date传入接口
            List<InnerMarketDomain> list = stockMarketIndexInfoMapper.getMarketInfo(inners, lastDate);
            return R.ok(list);
        });
        return date;

    }

    //查询沪深两市最新的板块行情数据，并按照交易金额降序排序展示前10条记录
    @Override
    public R<List<StockBlockDomain>> getInnerAllLSstock() {
        //获取当前时间
        Date dateNow = DateTimeUtils.getLastDate4Stock(DateTime.now()).toDate();
      /*  //假数据完成数据获取后删除
        dateNow= DateTime.parse("2021-12-21 14:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
      */  List<StockBlockDomain> list =stockBlockRtInfoMapper.selectNewData(dateNow);
        if (list.isEmpty()){
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        return R.ok(list);
    }
    //查询沪深两市最新的板块行情数据，并按照涨幅降序排序分页展示每页10条记录
    @Override
    public R<PageResult> getStockUpdownPageInfo(Integer page, Integer pageSize) {
//1.设置PageHelper分页参数
        PageHelper.startPage(page,pageSize);
        //2.获取当前最新的股票交易时间点
        Date curDate = DateTimeUtils.getLastDate4Stock(DateTime.now()).toDate();
        /*//todo
        curDate= DateTime.parse("2022-07-07 14:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
       */ //3.调用mapper接口查询
        List<StockUpdownDomain> infos= stockRtInfoMapper.getNewestStockInfo(curDate);
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //4.组装PageInfo对象，获取分页的具体信息,因为PageInfo包含了丰富的分页信息，而部分分页信息是前端不需要的
        //PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(infos);
//        PageResult<StockUpdownDomain> pageResult = new PageResult<>(pageInfo);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(new PageInfo<>(infos));
        //5.封装响应数据
        return R.ok(pageResult);
    }

    //将指定页的股票数据导出到excel表下
    @Override
    public void exportStockUpdown(Integer page, Integer pageSize, HttpServletResponse response) throws IOException {
        R<PageResult> stockUpdownPageInfo = this.getStockUpdownPageInfo(page, pageSize);
        List<StockUpdownDomain> data = stockUpdownPageInfo.getData().getRows();
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class).sheet("模板").doWrite(data);
    }

    /**
     * 统计A股大盘T日和T-1日成交量对比功能
     */
    @Override
    public R<Map> selectTradeAmtContrast() {
        //获取当前时间
        DateTime dateNow = DateTimeUtils.getLastDate4Stock(DateTime.now());
        //获取当日开盘时间
        DateTime open = DateTimeUtils.getOpenDate(dateNow);
        //转换为date类型
        Date dateNowDate = dateNow.toDate();
        Date openDate = open.toDate();
        //1.2 获取T-1日的区间范围
        //获取dateNow的上一个股票有效交易日
        DateTime preLastDateTime = DateTimeUtils.getPreviousTradingDay(dateNow);
        DateTime preOpenDateTime = DateTimeUtils.getOpenDate(preLastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4PreT = preOpenDateTime.toDate();
        Date endTime4PreT=preLastDateTime.toDate();
       /* //TODO fake data后期去掉
        dateNowDate = DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        openDate=DateTimeUtils.getOpenDate(DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))).toDate();
        //TODO  mock数据
        startTime4PreT=DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        endTime4PreT=DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();*/
        //查询A盘当前时间成交量信息
        //2.1 获取大盘的id集合
        List<String> markedIds = stockInfoConfig.getInner();
        //2.2 查询当前时间成交量信息和T-1日成交量信息
        List<Map> currentList=stockMarketIndexInfoMapper.selectTradeAmtContrast(markedIds,dateNowDate,openDate);
        List<Map>beforeCurrentList=stockMarketIndexInfoMapper.selectTradeAmtContrast(markedIds,endTime4PreT,startTime4PreT);
        HashMap<String, List> info = new HashMap<>();
        info.put("amtList",currentList);
        info.put("yesAmtList",beforeCurrentList);
        return R.ok(info);
    }

    /**
     * 统计当前时间下，A股在各个涨跌区间股票的数量
     */
    @Override
    public R<Map> getRFStockinfo() {
        //获取当前时间
        DateTime date = DateTimeUtils.getLastDate4Stock(DateTime.now());
        Date dateNow = date.toDate();
        /*//TODO mock测试数据
        dateNow=DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
      */  List<Map> list=stockMarketIndexInfoMapper. getRFStockinfo(dateNow);
        HashMap<String, Object> data = new HashMap<>();
        //格式化date数据
        String curDateStr = new DateTime(dateNow).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        data.put("time",curDateStr);
        data.put("infos",list);
        return R.ok(data);
    }

    /**
     * 查询个股的分时行情数据，统计指定股票T日每分钟的交易数据
     */
    @Override
    public R<List> getStockScreenTimeSharing(String code) {
        //获取当前时间
        DateTime date = DateTimeUtils.getLastDate4Stock(DateTime.now());
        //获取当前开票时间
        DateTime open = DateTimeUtils.getOpenDate(date);
        //转换为date类型
        Date dateNow = date.toDate();
        Date startdate = open.toDate();
       /* //todo mock数据
        dateNow=DateTime.parse("2021-12-30 14:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        startdate=DateTime.parse("2021-12-30 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
       */ //查询指定股票的分时数据
        List<Stock4MinuteDomain> data=stockRtInfoMapper.getStockScreenTimeSharing(code,startdate,dateNow);

        if (CollectionUtils.isEmpty(data)){
             data = new ArrayList<>();
        }
        return R.ok(data);
    }

    /**
     * 查询指定股票每天产生的数据，组成日k线数据
     * 默认查询10天的数据
     */
    @Override
    public R<List> getStockScreenDKline(String code) {
      /*  //获取当前时间
        DateTime listDate = DateTimeUtils.getLastDate4Stock(DateTime.now());
        Date date = listDate.toDate();
        //获取10天前的时间
        DateTime openDate = listDate.minusDays(10);
        Date startDate = openDate.toDate();
        //todo mock数据
        date=DateTime.parse("2022-01-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        startDate=DateTime.parse("2022-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //查询指定股票的list数据
        *//*List<Date> dates = stockRtInfoMapper.getTimeList(code, startDate, date);
        List<Stock4EvrDayDomain> data=stockRtInfoMapper.getStockScreenDKline(code,dates);*//*
        List<Date> closeDates = stockRtInfoMapper.findMaxCloseDatesByCodeAndDateRange(code, startDate, date);
        List<Stock4EvrDayDomain> data = stockRtInfoMapper.getStockScreenDKline(code, closeDates);
        //判断是否为空
        if (CollectionUtils.isEmpty(data)){

            return R.error(ResponseCode.DATA_NOT_EXISTS);
        }
        //返回数据
        return R.ok(data);*/
        //1.1 获取截止时间
        DateTime endDateTime = DateTimeUtils.getLastDate4Stock(DateTime.now());
        Date endTime = endDateTime.toDate();
        /*//TODO MOCKDATA
        endTime=DateTime.parse("2022-01-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
      */  //1.2 获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        Date startTime = startDateTime.toDate();
       /* //TODO MOCKDATA
        startTime=DateTime.parse("2022-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
       */ //2.调用mapper接口获取查询的集合信息-方案1
//        List<Stock4EvrDayDomain> data= stockRtInfoMapper.getStockInfo4EvrDay(code,startTime,endTime);
        //方案2：先获取指定日期范围内的收盘时间点集合
        List<Date> closeDates = stockRtInfoMapper.getCloseDates(code, startTime, endTime);
        //根据收盘时间获取日K数据
        List<Stock4EvrDayDomain> data = stockRtInfoMapper.getStockCreenDkLineData(code, closeDates);
        //3.组装数据，响应
        return R.ok(data);
    }

    /**
     * 外盘指数行情数据查询，根据时间和大盘点数降序取前四
     * @return
     */
    @Override
    public R<List> outerAllStock() {
        //获取外盘指数数据
        List<outerStockDomain> data=stockOuterMarketIndexInfoMapper.getOuterAllStock();

        return R.ok(data);
    }

    /**
     *  根据输入的个股代码模糊搜索
     */
    @Override
    public R<List> vagueSearch(String searchStr) {
        //进行模糊查询
        List<vagueSearchRtDomain> data =stockRtInfoMapper.vagueSearch(searchStr);
        return R.ok(data);
    }

    /**
     * 个股主营业务查询接口
     */
    @Override
    public oneStockBusiness getStockBusiness(String code) {
       oneStockBusiness data= stockBusinessMapper.getOneStockBusiness(code);
        return data;
    }



    /**
     * 个股周k线
     */
    @Override
    public R<weekStockK> getStockScreenWeekKline(String code) {
        if (code.isEmpty()){
            R.error(ResponseCode.DATA_NOT_EXISTS);
        }
        //获取当前时间
        Date date = DateTimeUtils.getLastDate4Stock(DateTime.now()).toDate();
        //获取周一开盘的时间
        LocalDate localDate = LocalDate.now();
        LocalDate start = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Date startTime = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        DateTime time = new DateTime(startTime);
        //周一 的开盘时间
        Date openDate = DateTimeUtils.getOpenDate(time).toDate();
        //进行平均价max和min价和编码
        zhoukamm data = stockRtInfoMapper.getStockScreenWeekKline(code, openDate, date);
        BigDecimal maxPrice = data.getMaxPrice();
        //查询最高价时间
        Date mxTime =stockRtInfoMapper.getMxTime(code, openDate, date, maxPrice);
        //查询周一开盘价
        BigDecimal openPrice = stockRtInfoMapper.getOpenPrice(code, openDate);
        //查询周五收盘价
        BigDecimal closePrice = stockRtInfoMapper.getClosePrice(code,  date);
//        响应数据组装
        weekStockK weekK = new weekStockK();
        BeanUtils.copyProperties(data ,weekK);
        weekK.setMxTime(mxTime);
        weekK.setOpenPrice(openPrice);
        weekK.setClosePrice(closePrice);

        System.out.println(weekK);
        return R.ok(weekK);
    }

}
