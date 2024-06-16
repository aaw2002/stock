package com.itheima.stock.domain.vo.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRespVo {

    //用户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //用户名
    private String username;
    //昵称
    private String nickName;
    //手机号
    private String phone;

}
