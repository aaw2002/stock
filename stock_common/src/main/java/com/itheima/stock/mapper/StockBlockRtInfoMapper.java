package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.StockBlockDomain;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author aianwei
* @description 针对表【stock_block_rt_info(股票板块详情信息表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:30
* @Entity com.itheima.pojo.entity.StockBlockRtInfo
*/
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);

//查询沪深两市最新的板块行情数据并按照交易金额降序排序展示前10条记录
    List<StockBlockDomain> selectNewData(@Param("dateNow") Date dateNow);

    /**
     * 进行板块数据插入
     * @param infos
     */
    void insertBatch(@Param("infos") List<StockBlockRtInfo> infos);
}
