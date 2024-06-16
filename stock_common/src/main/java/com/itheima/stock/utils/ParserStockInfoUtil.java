package com.itheima.stock.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.pojo.vo.ParseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class ParserStockInfoUtil {
    public ParserStockInfoUtil(SnowflakeIdWorker idWorker) {
        this.idWorker = idWorker;
    }

    private SnowflakeIdWorker idWorker;

    /**
     * @param stockStr 大盘 股票 实时拉去原始数据(js格式解析)
     * @param type     1:国内大盘 2.国外大盘 3.A股
     * @return 解析后的数据
     */
    public List parser4StockOrMarketInfo(String stockStr, Integer type) {

        //收集封装数据
        List<Object> datas = new ArrayList<>();
        //合法判断
        if (Strings.isBlank(stockStr)) {
            //返回空数组
            return datas;
        }
        if (type == ParseType.INNER) {
            //去除字符串中的\n字符
            stockStr = stockStr.replace("\n", "");
            //获取国内大盘数据
            String[] split = stockStr.split(";");
            //封装对象
//            List<StockMarketIndexInfo> list = new ArrayList<>();
            for (String s : split) {
                if (s.contains("var hq_str_")) {
                    String[] split1 = s.split(",");
                    String codeAndName = split1[0];
                    int eqIndex = codeAndName.indexOf('=');
                    if (eqIndex != -1) {
                        Long id = this.idWorker.nextId();

                        String code = codeAndName.substring(11, eqIndex).replace("\"", ""); // 提取并移除代码部分的双引号
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
                        datas.add(info);
                    }
                }
            }

        }
        if (type == ParseType.OUTER) {
            //去除字符串中的\n字符
            stockStr = stockStr.replace("\n", "");
            //获取国外大盘数据
            String[] split = stockStr.split(";");
            for (String s : split) {
                if (s.contains("var hq_str_")) {
                    String[] split1 = s.split(",");
                    String codeAndName = split1[0];
                    int eqIndex = codeAndName.indexOf('=');
                    if (eqIndex != -1) {
                        Long id = this.idWorker.nextId();

                        String code = codeAndName.substring(11, eqIndex).replace("\"", ""); // 提取并移除代码部分的双引号
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
                        datas.add(info);
                    }
                }
            }

        }
        if (type == ParseType.ASHARE) {
            //去除字符串中的\n字符
            stockStr = stockStr.replace("\n", "");
            //获取国内个股大盘数据
            String[] split = stockStr.split(";");

            for (String s : split) {
                if (s.contains("var hq_str_")) {
                    String[] split1 = s.split(",");
                    String codeAndName = split1[0];
                    int eqIndex = codeAndName.indexOf('=');
                    if (eqIndex != -1) {
                        Long id = this.idWorker.nextId();

                        String code = codeAndName.substring(13, eqIndex).replace("\"", ""); // 提取并移除代码部分的双引号
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
                        //组装entity对象
                        StockRtInfo info = StockRtInfo.builder()
                                .id(id)
                                .stockCode(code)
                                .stockName(name)
                                .curPrice(curPoint)
                                .openPrice(openPoint)
                                .preClosePrice(preClosePoint)
                                .maxPrice(maxPoint)
                                .minPrice(minPoint)
                                .tradeVolume(tradeVol)
                                .tradeAmount(tradeAmt)
                                .curTime(curTime)
                                .build();
                        //收集封装的对象，方便批量插入
                        datas.add(info);
                    }
                }
            }

        }
        return datas;
    }

    /**
     * 板块业务数据实时拉取
     */
    public List<StockBlockRtInfo> getStockBlockData(String str) {
        // 去掉开头和结尾
        str=str.replace("var S_Finance_bankuai_sinaindustry = ","");
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(str, JsonObject.class);
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
            return blocks;
    }
}
