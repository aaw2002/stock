package com.itheima.stock.service.impl;

import com.google.common.collect.Lists;
import com.itheima.stock.config.TaskExecutePool;
import com.itheima.stock.mapper.StockBlockRtInfoMapper;
import com.itheima.stock.mapper.StockBusinessMapper;
import com.itheima.stock.mapper.StockMarketIndexInfoMapper;
import com.itheima.stock.mapper.StockRtInfoMapper;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.pojo.vo.ParseType;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockTimerTaskService;
import com.itheima.stock.utils.DateTimeUtils;
import com.itheima.stock.utils.ParserStockInfoUtil;
import com.itheima.stock.utils.SnowflakeIdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class  StockTimerTaskServiceImpl implements StockTimerTaskService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TaskExecutePool taskExecutePool;
    /**
     * 获取国内大盘的实时数据信息
     */
    @Override
    public void getInnerMarketData() {
        HttpHeaders headers = new HttpHeaders();
        // 请求地址
        String url =stockInfoConfig.getUrl()+String.join(",",stockInfoConfig.getInner());
        //防盗链
        String xlReferer=stockInfoConfig.getReferer();
        // 访问来源
        String xlUserAgent=stockInfoConfig.getUserAgent();
        headers.add("Referer", xlReferer);
        headers.add("User-Agent", xlUserAgent);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> inners = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (inners.getStatusCodeValue()!=200)
        {
            log.error("采集国内大盘的实时数据信息失败,状态码{},时间{}",inners.getStatusCodeValue(), DateTime.now().toString("yyyy-MM-dd HH-mm"));
            return;
        }
        String innersBody = inners.getBody();
        log.info("采集国内大盘的实时数据信息成功,数据{},时间{}",innersBody,DateTime.now().toString("yyyy-MM-dd HH-mm"));
//        String reg="var hq_str_(.+)=\"(.+)\";";
        //去除字符串中的\n字符
        innersBody=innersBody.replace("\n", "");
        String[] split = innersBody.split(";");
        //封装对象
        List<StockMarketIndexInfo> list = new ArrayList<>();
        //进行字符串解析切割
        for (String s : split) {
            if (s.contains("var hq_str_"))
            {
                String[] split1 = s.split(",");
                String codeAndName = split1[0];
                int eqIndex = codeAndName.indexOf('=');
                if (eqIndex!=-1) {
                    Long id = snowflakeIdWorker.nextId();

                    String code =  codeAndName.substring(11, eqIndex).replace("\"", ""); // 提取并移除代码部分的双引号
                    String name = codeAndName.substring(eqIndex + 2, codeAndName.length()).replace("\"", ""); // 提取并移除名称部分的双引号
//                    String name = split1[1];
                    BigDecimal openPoint = new BigDecimal(split1[2]);
                    BigDecimal preClosePoint = new BigDecimal(split1[3]);
                    BigDecimal curPoint = new BigDecimal(split1[4]);
                    BigDecimal maxPoint = new BigDecimal(split1[5]);
                    BigDecimal minPoint = new BigDecimal(split1[6]);
                    Long tradeAmt = new Long(split1[8]);
                    BigDecimal tradeVol = new BigDecimal(split1[9]);
//                    Date curTime = DateTimeUtils.getDateTimeWithoutSecond(split1[31]).toDate();
                    Date curTime = DateTimeUtils.getDateTimeWithoutSecond(split1[30] + " " + split1[31]).toDate();
                    log.info(curTime.toString());
                    log.info("采集国内大盘的实时数据信息成功,数据{},时间{}", innersBody, DateTime.now().toString("yyyy-MM-dd HH:mm"));

                    //组装entity对象
                    StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                            .id(id)
                            .marketCode(code)
                            .marketName(name)
                            .curPoint(curPoint)
                            .openPoint(openPoint)
                            .preClosePoint(preClosePoint)
                            .maxPoint(maxPoint)
                            .minPoint(minPoint)
                            .tradeVolume(tradeVol)
                            .tradeAmount(tradeAmt)
                            .curTime(curTime)
                            .build();
                    //收集封装的对象，方便批量插入
                    list.add(info);
                    //批量插入
                    log.info("采集的当前大盘数据：{}",list);
                    if (CollectionUtils.isEmpty(list)) {
                        return;
                    }

                }

            }
        }
        stockMarketIndexInfoMapper.insertIneerBanch(list);
//        通知后台终端刷新本地缓存，发送的日期数据是告知对方当前更新的股票数据所在时间点
        rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());
    }

    /**
     * 获取国内个股的实时数据信息,并批量插入
     */
    @Override
    public void getStockRtIndex() {
        List<String> stockIds = stockBusinessMapper.getStockIds();
        //计算出符合sina命名规范的股票id数据
        stockIds = stockIds.stream().map(id -> {
            return id.startsWith("6") ? "sh" + id : "sz" + id;
        }).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        //防盗链
        String xlReferer=stockInfoConfig.getReferer();
        // 访问来源
        String xlUserAgent=stockInfoConfig.getUserAgent();
        headers.add("Referer", xlReferer);
        headers.add("User-Agent", xlUserAgent);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        Lists.partition(stockIds, 10).forEach(list -> {
            //拼接股票url地址
            String url=stockInfoConfig.getUrl()+String.join(",",list);
            //获取响应数据
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (exchange.getStatusCodeValue()!=200)
            {
                log.error("采集国内个股的实时数据信息失败,状态码{},时间{}",exchange.getStatusCodeValue(), DateTime.now().toString("yyyy-MM-dd HH-mm"));
                return;
            }
            String str = exchange.getBody();
            List<StockRtInfo> infos = parserStockInfoUtil.parser4StockOrMarketInfo(str, ParseType.ASHARE);
            System.out.println(infos);
            stockRtInfoMapper.insertBatch(infos);
        });
    }

    /**
     * 获取国内板块的实时数据信息,并批量插入库
     */
    @Override
    public void getStockBlockData() {
        HttpHeaders headers = new HttpHeaders();
        //防盗链
        String xlReferer=stockInfoConfig.getReferer();
        // 访问来源
        String xlUserAgent=stockInfoConfig.getUserAgent();
        //请求头
        headers.add("Referer", xlReferer);
        headers.add("User-Agent", xlUserAgent);
        //请求体
        HttpEntity<Object> entity = new HttpEntity<>(headers);
       String url=stockInfoConfig.getBlockUrl();
       //发送请求
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String str = exchange.getBody();
        List<StockBlockRtInfo> infos = parserStockInfoUtil.getStockBlockData(str);
        //进行数据插入
        stockBlockRtInfoMapper.insertBatch(infos);
    }

    @Override
    public void stockRtInto() {
        //模拟网络I/O  1000毫秒
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
