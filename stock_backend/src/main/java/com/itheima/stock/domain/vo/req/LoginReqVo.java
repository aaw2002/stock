package com.itheima.stock.domain.vo.req;

import lombok.Data;

@Data
public class LoginReqVo {

    private String username; //用户名
    private String password; //密码
     //验证吗
    private String code;
    //sessionID
    private String sessionId;
}
