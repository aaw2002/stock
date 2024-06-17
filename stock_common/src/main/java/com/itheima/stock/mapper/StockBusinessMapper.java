package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.oneStockBusiness;
import com.itheima.stock.pojo.entity.StockBusiness;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author aianwei
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:30
* @Entity com.itheima.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);
    /**
     * 获取所有股票的code
     * @return
     */
    List<String> getStockIds();

    /**
     *   个股主营查询
     */
    oneStockBusiness getOneStockBusiness(@Param("code") String code);

}
