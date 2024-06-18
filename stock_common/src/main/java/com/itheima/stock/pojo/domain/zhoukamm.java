package com.itheima.stock.pojo.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class zhoukamm {
    //周K线数据
    BigDecimal avgPrice;
    //周最高价
    BigDecimal maxPrice;
    //周最低价
    BigDecimal minPrice;
}
