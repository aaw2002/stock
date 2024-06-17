package com.itheima.stock.pojo.domain;

import lombok.Data;

@Data
public class oneStockBusiness {
    //公司名称
    String name;
    //股票编码
    String code;
    //行业板块名字
    String trade;
    //公司主营业务
    String business;
}
