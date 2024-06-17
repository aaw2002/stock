package com.itheima.stock.controller;

import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.service.StockService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quot")
@Api(tags = "个股模糊查询")
public class vagueSerchController {
    @Autowired
    private StockService stockService;
    /**
     *  根据输入的个股代码模糊搜索
     */
    @GetMapping("/stock/search")
    public R<List> vagueSearch(@RequestParam String searchStr){


        return  stockService.vagueSearch(searchStr);
    }
}
