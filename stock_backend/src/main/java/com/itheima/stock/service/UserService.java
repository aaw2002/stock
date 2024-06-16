package com.itheima.stock.service;

import com.itheima.stock.domain.vo.req.LoginReqVo;
import com.itheima.stock.domain.vo.resp.LoginRespVo;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.pojo.entity.SysUser;

import java.util.Map;

public interface UserService {
    SysUser getUserByUsername(String username);

    R<LoginRespVo> login(LoginReqVo loginReqVo);

    R<Map> getCaptchaCode();
}
