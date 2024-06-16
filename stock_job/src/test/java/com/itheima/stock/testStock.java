package com.itheima.stock;

import com.github.pagehelper.Page;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itheima.stock.mapper.StockBusinessMapper;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.stock.pojo.entity.StockBusiness;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockTimerTaskService;

import com.itheima.stock.utils.DateTimeUtils;
import com.itheima.stock.utils.ParserStockInfoUtil;
import com.itheima.stock.utils.SnowflakeIdWorker;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
//@SpringBootConfiguration
public class testStock {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockTimerTaskService stockTimerTaskService;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private SnowflakeIdWorker idWorker;

    @Test
public void test1(){
       stockTimerTaskService.getInnerMarketData();

}
    @Test
    public void test02(){
        List<String> codes = stockBusinessMapper.getStockIds();
        System.out.println(codes);
    }
    @Test
    public void test03(){
        HttpHeaders headers = new HttpHeaders();
        String url="https://hq.sinajs.cn/list=sh601003,sh601001";
        headers.add("Referer","https://finance.sina.com.cn/");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0");
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = exchange.getBody();
        List listdate = parserStockInfoUtil.parser4StockOrMarketInfo(body,3);
        System.out.println(listdate);
    }
    // 测试批量插入
    @Test
    public void test04(){
       stockTimerTaskService.getStockRtIndex();
    }

    @Test
    public void BlockTest(){
        HttpHeaders headers = new HttpHeaders();
        String url="https://vip.stock.finance.sina.com.cn/q/view/newSinaHy.php";
        headers.add("Referer",stockInfoConfig.getReferer());
        headers.add("User-Agent",stockInfoConfig.getUserAgent());
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = exchange.getBody();
        // 去掉开头和结尾
        body=body.replace("var S_Finance_bankuai_sinaindustry = ","");
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        List<StockBlockRtInfo> blocks = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()){
            //雪花生成id
            Long id = idWorker.nextId();
            // 获取时间
            DateTime date = DateTime.now();
            Date curtime = DateTimeUtils.getDateTimeWithoutSecond(date).toDate();
            // 获取数据
            String value=entry.getValue().getAsString();
            //进行切割
            value = value.replace("new_", "");
            String[] splits = value.split(",");
            //封装数据
            StockBlockRtInfo info = StockBlockRtInfo.builder()
                    .id(id)
                    .label(splits[0])
                    .blockName(splits[1])
                    .companyNum(Integer.valueOf(splits[2]))
                    .avgPrice(new BigDecimal(splits[3]))
                    .updownRate(new BigDecimal(splits[5]))
                    .tradeAmount(Long.valueOf(splits[6]))
                    .tradeVolume(new BigDecimal(splits[7]))
                    .curTime(curtime)
                    .build();
            blocks.add(info);
            };
        System.out.println(blocks);

        }
    @Test
    public void BlockTest2(){
        stockTimerTaskService.getStockBlockData();
    }



    }

