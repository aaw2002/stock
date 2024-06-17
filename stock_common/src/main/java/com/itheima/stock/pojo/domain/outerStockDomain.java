package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class outerStockDomain {
    /**
     * 大盘名称
     */
    private String name;

    /**
     * 大盘当前点
     */
    private BigDecimal curPoint;

    /**
     * 大盘涨跌值
     */
    private BigDecimal updown;

    /**
     * 大盘涨幅
     */
    private BigDecimal rose;

    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date curTime;
}
