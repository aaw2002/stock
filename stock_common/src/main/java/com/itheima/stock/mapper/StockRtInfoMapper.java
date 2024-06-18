package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author aianwei
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:31
* @Entity com.itheima.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    //查询沪深两市最新的板块行情数据，并按照涨幅降序排序分页展示每页10条记录
    List<StockUpdownDomain> getNewestStockInfo(@Param("curDate") Date curDate);
    /**
     * 查询个股的分时行情数据，统计指定股票T日每分钟的交易数据
     */
    List<Stock4MinuteDomain> getStockScreenTimeSharing(@Param("code") String code, @Param("startDate") Date startdate, @Param("dateNow") Date dateNow);


    /**
     * 查询指定股票每天产生的数据，组成日k线数据
     * 默认查询10天的数据
     */

/*
    List<Stock4EvrDayDomain> getStockScreenDKline(@Param("code") String code,@Param("maxCloseDates") List<Date> maxCloseDates);
    List<Date> findMaxCloseDatesByCodeAndDateRange(@Param("code") String code, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
*/
    /**
     * 获取指定日期范围内的收盘日期
     * @param code 股票编码
     * @param beginDate 起始时间
     * @param endDate 结束时间
     * @return
     */
    List<Date> getCloseDates(@Param("code") String code,
                             @Param("beginDate") Date beginDate,
                             @Param("endDate") Date endDate);
    /**
     * 获取指定股票在指定日期点下的数据
     * @param code 股票编码
     * @param dates 指定日期集合
     * @return
     */
    List<Stock4EvrDayDomain> getStockCreenDkLineData(@Param("code") String code,
                                                     @Param("dates") List<Date> dates);

    /**
     * 批量插入
     * @param infos
     */
    void insertBatch(@Param("infos") List<StockRtInfo> infos);

    //根据个股代码进行模糊查询
    List<vagueSearchRtDomain> vagueSearch(@Param("searchStr") String searchStr);

    //获取指定个股的周K线数据
    zhoukamm getStockScreenWeekKline(@Param("code") String code, @Param("openDate") Date openDate, @Param("date") Date date);

    //查询周k线最价时间
    Date getMxTime(@Param("code") String code, @Param("openDate") Date openDate, @Param("date") Date date, @Param("maxPrice") BigDecimal maxPrice);

    //查询周k周一开盘价
    BigDecimal getOpenPrice(@Param("code") String code, @Param("openDate") Date openDate);

    //查询周k周五收盘价若未到则查询当前价
    BigDecimal getClosePrice(@Param("code") String code, @Param("date") Date date);
}
