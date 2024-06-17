package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.outerStockDomain;
import com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo;

import java.util.List;

/**
* @author aianwei
* @description 针对表【stock_outer_market_index_info(外盘详情信息表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:30
* @Entity com.itheima.pojo.entity.StockOuterMarketIndexInfo
*/
public interface StockOuterMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockOuterMarketIndexInfo record);

    int insertSelective(StockOuterMarketIndexInfo record);

    StockOuterMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockOuterMarketIndexInfo record);

    int updateByPrimaryKey(StockOuterMarketIndexInfo record);

    //获取外盘大盘，根据时间降序和大盘点数降序
    List<outerStockDomain> getOuterAllStock();
}
