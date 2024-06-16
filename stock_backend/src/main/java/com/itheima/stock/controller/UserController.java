package com.itheima.stock.controller;

import com.itheima.stock.domain.vo.resp.LoginRespVo;
import com.itheima.stock.domain.vo.resp.R;
import com.itheima.stock.service.UserService;
import com.itheima.stock.domain.vo.req.LoginReqVo;
import com.itheima.stock.pojo.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Api(tags = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("根据用户名查询用户信息")
    @GetMapping("user/{username}")
    public SysUser getUserByUsername(@PathVariable String username){
        SysUser userByUsername = userService.getUserByUsername(username);
        System.out.println(userByUsername);
        return userByUsername;
    }
    @ApiOperation("用户登录")
    @PostMapping("login")
    public R<LoginRespVo> login(@RequestBody LoginReqVo loginReqVo){
        return userService.login(loginReqVo);
    }
    /**
     * 生成登录校验码的访问接口
     * @return
     */
    @ApiOperation("生成登录校验码的访问接口")
    @GetMapping("/captcha")
    public R<Map> getCaptchaCode(){
        return userService.getCaptchaCode();
    }
}
