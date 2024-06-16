package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author aianwei
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:30
* @Entity com.itheima.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

/**
 * 获取国内最新所有大盘数据
 * 根据时间和和大盘编码
 */

    List<InnerMarketDomain> getMarketInfo(@Param("inners") List<String> inners, @Param("lastDate") Date lastDate);

    /**
     * 获取国内最新所有大盘数据
     * 根据时间和和大盘编码
     */
    List<Map> selectTradeAmtContrast(@Param("markedIds") List<String> markedIds, @Param("dateNowDate") Date dateNowDate, @Param("openDate") Date openDate);

    /**
     * 统计当前时间下，A股在各个涨跌区间股票的数量
     */
    List<Map> getRFStockinfo(@Param("dateNow") Date dateNow);

    /**
     * 批量插入国内大盘数据
     * @param list
     */
    void insertIneerBanch(@Param("infos") List<StockMarketIndexInfo> list);
}
