package com.itheima.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;

import cn.hutool.captcha.LineCaptcha;

import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.domain.vo.resp.ResponseCode;
import com.itheima.stock.domain.vo.req.LoginReqVo;
import com.itheima.stock.domain.vo.resp.LoginRespVo;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.UserService;
import com.itheima.stock.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;




@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.getUserByUsername(username);
    }


    @Override
    public R<LoginRespVo> login(LoginReqVo loginReqVo) {
        if (loginReqVo==null|| StringUtils.isBlank(loginReqVo.getUsername()) ||loginReqVo.getPassword()==null||loginReqVo.getCode()==null) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        if (StringUtils.isBlank(loginReqVo.getSessionId())) {
            return R.error(ResponseCode.DATA_ERROR);
        }
        String code = redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + loginReqVo.getSessionId()).toString();
        //判断验证码是否存在
        if (StringUtils.isBlank(redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX+loginReqVo.getSessionId()).toString())) {
            return R.error(ResponseCode.DATA_ERROR);
        }
        if (!code.equals(loginReqVo.getCode())){
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }
        //查询数据库用户信息
        SysUser sysUser = sysUserMapper.getUserByUsername(loginReqVo.getUsername());
        if (sysUser==null){
            return  R.error(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        LoginRespVo respvo = new LoginRespVo();
        if (passwordEncoder.matches(loginReqVo.getPassword(),sysUser.getPassword())) {
            BeanUtils.copyProperties(sysUser, respvo);
            return R.ok(respvo);
        }
        return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
    }


    @Override
    public R<Map> getCaptchaCode() {
        // 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 40,4,5);
        //获取验证码
        String code = captcha.getCode();
        log.info("验证码：{}",code);
        //生成sessionId
        String sessionId = String.valueOf(snowflakeIdWorker.nextId());
        //写入redis
        redisTemplate.opsForValue().set(StockConstant.CHECK_PREFIX+sessionId,code,5, TimeUnit.MINUTES);
        //响应的data数据
        HashMap<String,Object> dataMap = new HashMap<>(); //定义一个hashmap封装数据
        dataMap.put("sessionId",sessionId);
        dataMap.put("imageData",captcha.getImageBase64());
        return R.ok(dataMap);
    }
}
