package com.itheima.stock.stock;

import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.pojo.domain.oneStockBusiness;
import com.itheima.stock.pojo.domain.outerStockDomain;
import com.itheima.stock.pojo.domain.weekStockK;
import com.itheima.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class outerStockTest {
    @Autowired
    private StockService   stockService;
    @Test
    public void outerAllTest() {

        R<List> listR = stockService.outerAllStock();
        List<outerStockDomain> list = (List<outerStockDomain>) listR.getData();
        System.out.println(list);
    }
    //测试个股模糊查询
    @Test
    public void vagueSearchTest() {
        R<List> listR = stockService.vagueSearch("60");

    }

    /**
     * 测试个股主营业务接口
     */
    @Test
    public void getStockBusinessTest() {
        oneStockBusiness stockBusiness = stockService.getStockBusiness("000002");
        R<oneStockBusiness> oneStockBusinessR = R.ok(stockBusiness);
        System.out.println(oneStockBusinessR);
    }

    @Test
    public void getStockScreenWeekKline() {
        stockService.getStockScreenWeekKline("000002");
    }
}
