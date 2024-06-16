package com.itheima.stock;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class testGson {
    @Test
    public void test01() {
        String jsonData = "var S_Finance_bankuai_sinaindustry = {" +
                "    \"new_blhy\": \"new_blhy,玻璃行业,19,10.333684210526,0.12052631578947,1.180108219531,224502813,2319147201,sz300395,4.828,34.520,1.590,菲利华\"," +
                "    \"new_cbzz\": \"new_cbzz,船舶制造,8,13.83375,0.04625,0.33544877606528,277346265,2829515498,sz002608,3.741,8.320,0.300,江苏国信\"" +
                "}";

        // 去除var S_Finance_bankuai_sinaindustry = 和末尾的分号
        jsonData = jsonData.replace("var S_Finance_bankuai_sinaindustry = {", "");
        jsonData = jsonData.substring(0, jsonData.length() - 1);
        System.out.println(jsonData);


    }}

