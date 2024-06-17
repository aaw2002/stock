package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class weekStockK {
    //周K线数据
    BigDecimal avgPrice;
    //周最高价
    BigDecimal maxPrice;
    //周最低价
    BigDecimal minPrice;
    //周开盘价
    BigDecimal openPrice;
    //周收盘价
    BigDecimal closePrice;
    //周股票代码
    String code;
    //一周内最大时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    Date mxTime;
}
