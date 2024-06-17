package com.itheima.stock.pojo.domain;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
//个股模糊查询值
public class vagueSearchRtDomain {

    //股票代码
    String code;
    //股票名称
    String name;
}
